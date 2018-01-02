package Exceptions;

public class WebServiceCallFailedException extends Exception{

    private String wsName;
    private String[] inputs;

    public WebServiceCallFailedException(String wsName, String[] inputs) {
        this.wsName = wsName;
        this.inputs = inputs;
    }

    @Override
    public String getMessage() {
        return  "webService call Failed: "+wsName+"\n"+
                "with inputs: "+ String.join(", ",inputs);
    }
}
