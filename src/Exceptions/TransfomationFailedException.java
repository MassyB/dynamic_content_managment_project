package Exceptions;

public class TransfomationFailedException  extends Exception{

    private String webServiceName;
    private String callResultFile;

    public TransfomationFailedException(String webServiceName, String callResultFile) {
        this.webServiceName = webServiceName;
        this.callResultFile = callResultFile;
    }

    @Override
    public String getMessage() {
        return "Transformation failed for file "+ callResultFile+" of webService "+webServiceName;
    }
}
