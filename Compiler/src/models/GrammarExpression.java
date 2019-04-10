package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrammarExpression {

    private String  name;
    private List<String> proudctionList;

    public GrammarExpression(String name,String production) {
        this.name = name;
        this.proudctionList =new ArrayList<>();
        this.proudctionList.addAll(Arrays.asList(production.split(" ")));
    }

    public String getName() {
        return name;
    }
    public String getProduction(int index){
        return proudctionList.get(index);
    }

    public List<String> getProudctionList() {
        return proudctionList;
    }
}
