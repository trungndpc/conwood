package vn.conwood.client.webhook;

import vn.conwood.client.webhook.zalo.ZaloWebhookMessage;

public abstract class ZaloEvent {
    public abstract boolean process(ZaloWebhookMessage zaloWebhookMessage);
}
