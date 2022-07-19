package vn.conwood.client.bot.script;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import vn.conwood.client.bot.StockPromotionSession;
import vn.conwood.client.bot.User;
import vn.conwood.client.webhook.WebhookSessionManager;
import vn.conwood.jpa.entity.promotion.StockPromotionEntity;
import vn.conwood.util.BeanUtil;

import java.util.stream.Collectors;

public class StockPromotionScript {
    private static final Logger LOGGER = LogManager.getLogger();
    private final static WebhookSessionManager WEBHOOK_SESSION_MANAGER = BeanUtil.getBean(WebhookSessionManager.class);

    private User user;
    private StockPromotionSession session;


    private ObjectMapper objectMapper = new ObjectMapper();

    public StockPromotionScript(User user) throws JsonProcessingException {
        this.user = user;
        initSession(user);
    }

    public void process(ZaloWebhookMessage msg) throws Exception {
        String waitingQuestionId = session.getWaitingQuestionId();
        if (waitingQuestionId != null) {
            try {
                Question question = initWaitingQuestion(waitingQuestionId);
                if (question == null) {
                    throw new Exception("can not init waiting question | " + waitingQuestionId);
                }
                boolean isAccept = question.ans(msg);
                //store session
                String jsonQuestion = this.objectMapper.writeValueAsString(question);
                PredictFootballSession.PredictFootballSS predictFootballSS = new PredictFootballSession.PredictFootballSS(question.getId(),
                        question.getClass(), new JSONObject(jsonQuestion));
                this.session.putQuestion(question.getId(), predictFootballSS);
                WEBHOOK_SESSION_MANAGER.saveSession(user.getUid(), this.session);

                if (isAccept) {
                    PredictFootballSession.PredictFootballSS questionSS = null;
                    if (question instanceof WhichMatchQuestion) {
                        WhichMatchQuestion whichMatchQuestion = (WhichMatchQuestion) question;
                        MatchBotEntity match = (MatchBotEntity) whichMatchQuestion.getUserAnswer();
                        askResultMatch(match, true);
                    } else if (question instanceof RePredictMatchQuestion) {
                        RePredictMatchQuestion rePredictMatchQuestion = (RePredictMatchQuestion) question;
                        Boolean isOk = (Boolean) rePredictMatchQuestion.getUserAnswer();
                        if (isOk) {
                            //ask which team
                            WhichTeamQuestion whichTeamQuestion = new WhichTeamQuestion(user, this.session.getMatch());
                            if (whichTeamQuestion.ask()) {
                                this.session.setWaitingQuestionId(whichTeamQuestion.getId());
                                questionSS = new PredictFootballSession.PredictFootballSS(whichTeamQuestion.getId(),
                                        whichTeamQuestion.getClass(), new JSONObject(this.objectMapper.writeValueAsString(whichTeamQuestion)));
                                this.session.putQuestion(whichTeamQuestion.getId(), questionSS);
                            }
                        } else {
                            ZaloService.INSTANCE.send(user.getFollowerId(), ZaloMessage.toTextMessage("INSEE đã ghi nhận kết quả dự đoán của Anh/Chị.\n" +
                                    "Hãy tận hưởng trận đấu và chờ đợi kết quả thôi nào!"));
                        }
                    } else if (question instanceof WhichTeamQuestion) {
                        WhichTeamQuestion whichTeamQuestion = (WhichTeamQuestion) question;
                        PredictMatchBotEntity predictMatchBot = (PredictMatchBotEntity) whichTeamQuestion.getUserAnswer();
                        if (predictMatchBot != null) {
                            this.session.setPredict(predictMatchBot);
                            complete();
                            int count = this.session.getCount();
                            count++;
                            this.session.setCount(count);
                            MatchBotEntity nextMatchOnDay = getNextMatchOnDay();
                            if (nextMatchOnDay != null && this.session.getCount() <= 1) {
                                askResultMatch(nextMatchOnDay, false);
                            } else {
                                CompletePredictMessage completePredictMessage = new CompletePredictMessage(user);
                                completePredictMessage.send();
                                this.session = null;
                                WEBHOOK_SESSION_MANAGER.clearSession(user.getUid());
                            }
                        }
                    }
                }
            } finally {
                if (this.session != null) {
                    WEBHOOK_SESSION_MANAGER.saveSession(user.getUid(), this.session);
                }
            }
        }
    }

    private void complete() {
        PredictMatchBotEntity predict = this.session.getPredict();
        PredictMatchFootballFormEntity entity = new PredictMatchFootballFormEntity();
        entity.setId(this.session.getPredictId());
        entity.setUserId(user.getUid());
        entity.setMatchId(predict.getMatch().getId());
        entity.setSeason(this.session.getSeason());
        entity.setPromotionId(this.session.getPromotionId());
        entity.setTeamWin(predict.getTeamWin());
        entity.setStatus(StatusForm.INIT);
        PREDICT_FOOTBALL_MATCH_SERVICE.createOrSave(entity);
    }

    public void start(StockPromotionEntity promotion) throws JsonProcessingException {

        List<MatchBotEntity> matchBotEntityList = top5MostRecent.stream().map(entity -> {
            MatchBotEntity matchBotEntity = new MatchBotEntity();
            matchBotEntity.setId(entity.getId());
            matchBotEntity.setTeamA(entity.getTeamOne());
            matchBotEntity.setTeamB(entity.getTeamTwo());
            return matchBotEntity;
        }).collect(Collectors.toList());
        WhichMatchQuestion whichMatchQuestion = new WhichMatchQuestion(user, matchBotEntityList);
        whichMatchQuestion.ask();
        this.session.setWaitingQuestionId(whichMatchQuestion.getId());
        JSONObject json = new JSONObject(this.objectMapper.writeValueAsString(whichMatchQuestion));
        this.session.putQuestion(whichMatchQuestion.getId(), new PredictFootballSession.PredictFootballSS(whichMatchQuestion.getId(),
                WhichMatchQuestion.class, json));
        this.session.setPromotionId(promotion.getId());
        this.session.setTimeStart(System.currentTimeMillis());
        this.session.setSeason(promotion.getSeason());
        WEBHOOK_SESSION_MANAGER.saveSession(user.getUid(), this.session);
        return;
    }

    private Question initWaitingQuestion(String id) throws JsonProcessingException {
        PredictFootballSession.PredictFootballSS predictFootballSS = session.getQuestions().get(id);
        switch (predictFootballSS.getZclass().getSimpleName()) {
            case "WhichMatchQuestion":
                return new WhichMatchQuestion(predictFootballSS.getJson());
            case "WhichTeamQuestion":
                return new WhichTeamQuestion(predictFootballSS.getJson());
            case "SummaryPredictMatchQuestion":
                return new SummaryPredictMatchQuestion(predictFootballSS.getJson());
            case "RePredictMatchQuestion":
                return new RePredictMatchQuestion(predictFootballSS.getJson());
            case "NextMatchQuestion":
                return new NextMatchQuestion(predictFootballSS.getJson());
        }
        return null;
    }

    private MatchBotEntity getNextMatchOnDay() {
        List<MatchFootballEntity> top5MostRecent =
                MATCH_FOOTBALL_SERVICE.findTop2MostRecent(this.session.getSeason());
        Optional<MatchFootballEntity> optional = top5MostRecent.stream()
                .filter(matchFootballEntity -> this.session.getMatch().getId() != matchFootballEntity.getId())
                .findFirst();
        if (optional.isPresent()) {
            MatchFootballEntity matchFootballEntity = optional.get();
            MatchBotEntity rs = new MatchBotEntity();
            rs.setId(matchFootballEntity.getId());
            rs.setTeamA(matchFootballEntity.getTeamOne());
            rs.setTeamB(matchFootballEntity.getTeamTwo());
            return rs;
        }
        return null;
    }

    private void askResultMatch(MatchBotEntity match, boolean isCheckPredict) throws JsonProcessingException {
        MatchFootballEntity matchFootballEntity = MATCH_FOOTBALL_SERVICE.get(match.getId());
        if (matchFootballEntity.getStatus() == MatchFootballStatus.PROCESSING) {
            ZaloService.INSTANCE.send(user.getFollowerId(), ZaloMessage.toTextMessage("Trận đấu đang diễn ra."));
            return;
        }

        if (matchFootballEntity.getStatus() == MatchFootballStatus.DONE) {
            ZaloService.INSTANCE.send(user.getFollowerId(), ZaloMessage.toTextMessage("Trận đấu đã kết thúc"));
            return;
        }

        this.session.setMatch(match);
        this.session.setPredict(null);
        this.session.setPredictId(null);
        PredictFootballSession.PredictFootballSS questionSS = null;
        PredictMatchFootballFormEntity predictEntity = PREDICT_FOOTBALL_MATCH_SERVICE.findByUserIdAndMatchId(user.getUid(), match.getId());
        if (predictEntity != null) {
            PredictMatchBotEntity predictMatchBot = new PredictMatchBotEntity(match, predictEntity.getTeamWin());
            predictMatchBot.setId(predictEntity.getId());
            this.session.setPredictId(predictEntity.getId());
            this.session.setPredict(predictMatchBot);
            if (isCheckPredict) {
                RePredictMatchQuestion rePredictMatchQuestion = new RePredictMatchQuestion(user, predictMatchBot);
                if (rePredictMatchQuestion.ask()) {
                    this.session.setWaitingQuestionId(rePredictMatchQuestion.getId());
                    questionSS = new PredictFootballSession.PredictFootballSS(rePredictMatchQuestion.getId(),
                            rePredictMatchQuestion.getClass(), new JSONObject(this.objectMapper.writeValueAsString(rePredictMatchQuestion)));
                    this.session.putQuestion(rePredictMatchQuestion.getId(), questionSS);
                }
                return;
            }
        }
        WhichTeamQuestion whichTeamQuestion = new WhichTeamQuestion(user, match);
        if (whichTeamQuestion.ask()) {
            this.session.setWaitingQuestionId(whichTeamQuestion.getId());
            questionSS = new PredictFootballSession.PredictFootballSS(whichTeamQuestion.getId(),
                    whichTeamQuestion.getClass(), new JSONObject(this.objectMapper.writeValueAsString(whichTeamQuestion)));
            this.session.putQuestion(whichTeamQuestion.getId(), questionSS);
        }
    }

    private void initSession(User user) {
        Object currentSession = WEBHOOK_SESSION_MANAGER.getCurrentSession(user.getUid());
        if (currentSession == null || !(currentSession instanceof StockPromotionSession)) {
            this.session = new StockPromotionSession();
        } else {
            this.session = (StockPromotionSession) currentSession;
        }
        WEBHOOK_SESSION_MANAGER.saveSession(user.getUid(), this.session);
    }
}
