package Exceptions;

public class WebServiceNotDefinedException extends Exception {

    private String webServiceName;

    public WebServiceNotDefinedException(String webServiceName) {
        this.webServiceName = webServiceName;
    }

    @Override
    public String getMessage() {
        return "webService "+webServiceName+" not defined";
    }
}
