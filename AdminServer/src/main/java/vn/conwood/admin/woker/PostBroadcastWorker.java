package vn.conwood.admin.woker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jobrunr.jobs.annotations.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.admin.config.AppConfig;
import vn.conwood.admin.message.PostBroadcastMessage;
import vn.conwood.admin.message.User;
import vn.conwood.admin.service.BroadcastService;
import vn.conwood.admin.service.PostService;
import vn.conwood.admin.service.UserService;
import vn.conwood.admin.woker.task.PostBroadcastTask;
import vn.conwood.common.status.StatusBroadcast;
import vn.conwood.common.status.StatusUser;
import vn.conwood.jpa.entity.BroadcastEntity;
import vn.conwood.jpa.entity.PostEntity;
import vn.conwood.jpa.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostBroadcastWorker {
    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private UserService userService;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private PostService postService;

    @Autowired
    private AppConfig appConfig;

    @Job(name = "POST_BROADCAST", retries = 1)
    public void execute(PostBroadcastTask task) {
        int totalUid = 0;
        int totalUidAfterBuildUser = 0;
        int totalSuccessSend = 0;
        int error = 0;
        BroadcastEntity broadcastEntity = broadcastService.get(task.getBroadcastId());
        try{
            totalUid = task.getUids().size();
            List<Integer> uids = task.getUids();
            List<User> users = uids.stream().map(this::getUser).filter(e -> e != null)
                    .collect(Collectors.toList());
            totalUidAfterBuildUser = users.size();
            PostEntity postEntity = postService.get(task.getPostId());
            for (User user: users) {
                PostBroadcastMessage postBroadcastMessage = new PostBroadcastMessage(user);
                postBroadcastMessage.setImg(postEntity.getCover());
                postBroadcastMessage.setLink(appConfig.CLIENT_DOMAIN + "/bai-viet/" + postEntity.getId() + "?brcId=" + broadcastEntity.getId());
                postBroadcastMessage.setSummary(postEntity.getSummary());
                postBroadcastMessage.setTitle(postEntity.getTitle());
                if (postBroadcastMessage.send()) {
                    totalSuccessSend++;
                }
            }
        }catch (Exception e) {
            error = -1;
            LOGGER.error(e.getMessage(), e);
        }finally {
            broadcastEntity.setTotalUids(totalUid);
            broadcastEntity.setTotalUidsAfterBuildUser(totalUidAfterBuildUser);
            broadcastEntity.setTotalUidsSuccessSend(totalSuccessSend);
            if (error == 0) {
                broadcastEntity.setStatus(StatusBroadcast.DONE);
            }else {
                broadcastEntity.setStatus(StatusBroadcast.FAILED);
            }
            broadcastService.update(broadcastEntity);
        }
    }

    private User getUser(int uid) {
        try{
            UserEntity userEntity = userService.findById(uid);
            if (userEntity.getStatus() != StatusUser.APPROVED || userEntity.getFollowerId() == null) {
                throw new Exception("user is not valid to send broadcast uid: " + uid);
            }
            return new User(userEntity.getId(), userEntity.getFollowerId(), userEntity.getName());
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

}
