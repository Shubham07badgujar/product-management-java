package exception;

public class InvalidInputException extends Exception {
    private String inputField;
    private String inputValue;
    private String validationRule;

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, String inputField, String inputValue, String validationRule) {
        super(message);
        this.inputField = inputField;
        this.inputValue = inputValue;
        this.validationRule = validationRule;
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getInputField() {
        return inputField;
    }

    public String getInputValue() {
        return inputValue;
    }

    public String getValidationRule() {
        return validationRule;
    }

    @Override
    public String toString() {
        if (inputField != null) {
            return String.format("❌ Invalid Input: %s\n🔍 Field: %s\n💡 Rule: %s\n📝 Your Input: '%s'",
                    getMessage(), inputField, validationRule, inputValue);
        }
        return "❌ " + getMessage();
    }
}