package net.optionfactory.springai;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;

/**
 * See: <a href="https://meninblack.fandom.com/wiki/Neuralyzer">explanation of the name</a>
 */
public class Neuralyzer {
    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

    /**
     * Forget the final part of the text counting token with GPT_4O model.
     *
     * @param text      input text
     * @param maxTokens max tokens to shrink the output
     * @return truncated text
     */
    public static String forget(String text, int maxTokens) {
        return forget(text, maxTokens, ModelType.GPT_4O);
    }

    /**
     * Forget the final part of the text counting token with specified model.
     *
     * @param text      input text
     * @param maxTokens max tokens to shrink the output
     * @param modelType specify the model type to use for token count
     * @return truncated text
     */
    public static String forget(String text, int maxTokens, ModelType modelType) {
        final var encoding = registry.getEncodingForModel(modelType);
        var currentString = text;
        while (encoding.countTokens(currentString) > maxTokens) {
            float avgTokenLength = (float) currentString.length() / encoding.countTokens(currentString);
            float maxTokensLength = avgTokenLength * maxTokens;
            if (maxTokensLength >= currentString.length()) {
                throw new IllegalStateException("Could not shorten string when truncating");
            }
            currentString = currentString.substring(0, (int) maxTokensLength);
        }
        return currentString;
    }


}
