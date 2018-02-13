package Exceptions;

/**
 * Exception for when an error occurs during the parsing of transformation file.
 * */

public class ParsingTransformedFileFailedException extends Exception {

    private String webServiceName;
    private String transformationFile;

    public ParsingTransformedFileFailedException(String webServiceName, String transformationFile) {
        this.webServiceName = webServiceName;
        this.transformationFile = transformationFile;
    }

    @Override
    public String getMessage() {
        return "Parsing of transformation file "+transformationFile+" failed for webService "+webServiceName;
    }
}
