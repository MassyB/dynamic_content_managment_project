import dataModel.Table;
import javafx.scene.control.Tab;
import queryEngine.Query;
import queryEngine.Workflow;

public class Main {

    public static void main(String[] args){

        Query query = Query.parse("t(?id,?aTitle,?rTitle,?aDate,?rlength)<-mb_getArtistInfoByName^iooo(\"Elvis Presley\",?id,?bDate,?eDate)#mb_getAlbumsArtistId^ioooo(?id,?aTitle,?aId,?aDate,?country)#mb_getSongByAlbumId^iooo(?aId,?rId,?rTitle,?rlength)");

        if(! query.isWellFormed()){
            System.out.println("query not well formed");
            return;
        }
        Workflow w = query.getWorkflow();

        if(! w.isAdmissible()){
            System.out.println("workflow not admissible");
            return;
        }

        if(! w.isExecutable()){
            System.out.println("workflow not executable: a ws is missing");
            return;
        }

        Table t;

        try {
            t =  w.execute();
            System.out.println("Success");
            System.out.println(t.getSize()+" rows ");
            System.out.println(t);

        }catch (Exception e){

            System.out.println("Error\n"+e.getMessage());
            e.printStackTrace();

        }
    }
}
