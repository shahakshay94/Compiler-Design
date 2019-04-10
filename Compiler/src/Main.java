import global.Constants;
import models.Visitors.CodeGeneration.StackBasedCodeGenerationVisitor;
import models.Visitors.ComputeMemSizeVisitor;
import models.Visitors.SymTabCreationVisitor;
import models.Visitors.TypeCheckingVisitor;
import utils.ASTManager;
import utils.BufferManager;
import utils.LexicalResponseManager;
import utils.TableParserBuilder;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        initializeParser(args);

        boolean success = Parser.getInstance().parse();

        startSecondPhase();

        if(success){
            System.out.println("Successfully Parsed Input");
        }else{
            System.out.println("Error while parsing Input");
        }
        LexicalResponseManager.getInstance().finisheWriting();
    }




    public static void startSecondPhase(){
        if(ASTManager.getInstance().getProgNode()==null){
            System.out.println("Could not able to create AST for given program!");
            return;
        }

        ASTManager.getInstance().getProgNode().accept(new SymTabCreationVisitor());

        System.out.println("==TYPE CHECKING PHASE STARTED======");

        ASTManager.getInstance().getProgNode().accept(new TypeCheckingVisitor());
        ASTManager.getInstance().getProgNode().accept(new ComputeMemSizeVisitor());
        ASTManager.getInstance().getProgNode().accept(new StackBasedCodeGenerationVisitor(Constants.OUTPUT_PROGRAM_M));

        System.out.println("==PRINTING TREE======");

//        ASTManager.getInstance().getProgNode().print();


        System.out.println("==PRINTING TABLE=====");
        System.out.println(ASTManager.getInstance().getProgNode().symtab);

        System.out.println("==TABLE PRINTED=======");

        ASTManager.getInstance().getProgNode().print( LexicalResponseManager.getInstance().getASTWriter());
        LexicalResponseManager.getInstance().getSymbolTableWriter().println(ASTManager.getInstance().getProgNode().symtab);

    }
    public static void initializeParser(String[] args) {

        createRequiredFolders();

        // get filepath from user input args
        File inputFile = null;
        if (args.length > 0) {
            inputFile = new File(args[0]);
        }
        // check if file is valid or not
        // if file is not valid take file from res/input/program.txt
        if (inputFile == null || !inputFile.isFile()) {
            inputFile = new File(Constants.INPUT_FILE_PATH);
        }
        TableParserBuilder.getInstance().buildParser();
        BufferManager.getInstance().initialize(inputFile);
    }

    private static void createRequiredFolders() {
        File resDir = new File("res");
        if(!resDir.isFile()){
            resDir.mkdir();
        }

        File inputDir = new File("res/input");
        if(!inputDir.isFile()){
            inputDir.mkdir();
        }

        File outputDir = new File("res/output");
        if(!outputDir.isFile()){
            outputDir.mkdir();
        }
        File assetsDir = new File("res/assets");
        if(!assetsDir.isFile()){
            assetsDir.mkdir();
        }
    }
}



