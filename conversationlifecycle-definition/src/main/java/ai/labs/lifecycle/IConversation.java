package ai.labs.lifecycle;

import ai.labs.memory.IConversationMemory;
import ai.labs.models.Context;

import java.util.Map;

/**
 * @author ginccc
 */
public interface IConversation {
    IConversationMemory getConversationMemory();

    void init(Map<String, Context> context) throws LifecycleException;

    boolean isEnded();

    void endConversation();

    void say(final String message, Map<String, Context> contexts)
            throws LifecycleException, ConversationNotReadyException;

    interface IConversationOutputRenderer {
        void renderOutput(IConversationMemory conversationMemory);
    }

    class ConversationNotReadyException extends Exception {
        public ConversationNotReadyException(String message) {
            super(message);
        }
    }
}
