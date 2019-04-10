package utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import global.Constants;
import models.Token;
import models.TokenType;

class ErrorMessage{
    private String type;
    private int lineNum;
    private int colNum;
    private String message;

    public String getType() {
        return type;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getColNum() {
        return colNum;
    }

    public String getMessage() {
        return message;
    }

    public ErrorMessage(String type, int lineNum, int colNum, String message) {
        this.type = type;
        this.lineNum = lineNum;
        this.colNum = colNum;
        this.message = message;
    }
}

public class LexicalResponseManager {
    private static LexicalResponseManager ourInstance = new LexicalResponseManager();
    private PrintWriter errorWriterFile;
    private PrintWriter aTOccWriterFile;
    private PrintWriter tokenWriterFile;
    private PrintWriter derivationWriterFile;
    private PrintWriter symbolTableWriter;
    private PrintWriter ASTWriter;
    private List<ErrorMessage> errorMessageList = new ArrayList<>();
    public static LexicalResponseManager getInstance() {
        return ourInstance;
    }

    public PrintWriter getSymbolTableWriter() {
        return symbolTableWriter;
    }

    public PrintWriter getASTWriter() {
        return ASTWriter;
    }

    private LexicalResponseManager() {
        try {
            tokenWriterFile = new PrintWriter(Constants.OUTPUT_TOKEN_GENERATED_FILE_PATH, "UTF-8");
            aTOccWriterFile = new PrintWriter(Constants.OUTPUT_ATOCC_FILE_NAME_PATH, "UTF-8");
            errorWriterFile = new PrintWriter(Constants.OUTPUT_ERROR_FILE_NAME_PATH, "UTF-8");
            derivationWriterFile = new PrintWriter(Constants.OUTPUT_DERIVATION, "UTF-8");
            symbolTableWriter = new PrintWriter(Constants.OUTPUT_SYMTABLE, "UTF-8");
            ASTWriter = new PrintWriter(Constants.OUTPUT_AST, "UTF-8");
            errorWriterFile.println("Format: TokenLineNumber : TokenColumnNumber , ErrorType , TokenValue");
            tokenWriterFile.println("Format: TokenType , TokenValue , TokenLineNumber : TokenColumnNumber");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void initialize() {

    }

    public void sortErrorMessageListBasedOnLineNum(){
        errorMessageList.sort(new Comparator<ErrorMessage>() {
            @Override
            public int compare(ErrorMessage o1, ErrorMessage o2) {
                return o1.getLineNum()-o2.getLineNum();
            }
        });
    }

    public void addErrorMessage(int lineNum,int colNum,String errorType,String message){
        System.out.println(errorType+" Error at "+lineNum+":"+colNum+" :"+message);
        ErrorMessage errorMessage = new ErrorMessage(errorType,lineNum,colNum,message);
        errorMessageList.add(errorMessage);
    }

    public void finalWriteErroListToFile(){
        sortErrorMessageListBasedOnLineNum();
        for(ErrorMessage errorMessage : errorMessageList){
            if(errorMessage.getLineNum()>0) {
                errorWriterFile.print(errorMessage.getLineNum());
                if(errorMessage.getColNum()>0) {
                    errorWriterFile.print(":");
                    errorWriterFile.print(errorMessage.getColNum());
                }
            }
            errorWriterFile.print(",");
            errorWriterFile.print(errorMessage.getType());
            errorWriterFile.print(",");
            errorWriterFile.print(errorMessage.getMessage());
            errorWriterFile.println();
        }
    }

    public void finisheWriting() {
        tokenWriterFile.close();
        aTOccWriterFile.close();
        derivationWriterFile.close();
        finalWriteErroListToFile();
        errorWriterFile.close();
        symbolTableWriter.close();
        ASTWriter.close();
    }

    public void addDerivationToFIle(List<String> dataList) {
        for (String data : dataList) {
            derivationWriterFile.print(data + " ");
        }
        derivationWriterFile.println();
    }

    public void writeSyntacticalMissingError(String expected,Token token) {
        if (token != null) {
            addErrorMessage(token.getLineNumber(),token.getColumnNumber(),"Syntax Error","Expected: "+expected+" but found: "+token.getTokenValue());
        }
    }
    public void writeSyntacticalError(Token token) {
        if (token != null) {
            addErrorMessage(token.getLineNumber(),token.getColumnNumber(),"Syntax Error",token.getTokenValue());
        }
    }

    public void writeLexicalResponseToFile(Token token) {
        if (token != null) {
            if (token.getTokenType() == TokenType.INVALID_IDENTIFIER || token.getTokenType() == TokenType.INVALID_NUMBER) {
                addErrorMessage(token.getLineNumber(),token.getColumnNumber(),"Syntax Error",token.getTokenValue());
                return;
            }

            // write atocc format to atocc file
            if (!token.getTokenValue().equals("/*") && !token.getTokenValue().equals("*/") && !token.getTokenValue().equals("//")) {
                if (token.getTokenType().equals(TokenType.ID) || token.getTokenType().equals(TokenType.INTEGER) || token.getTokenType().equals(TokenType.FLOAT)) {
                    aTOccWriterFile.print(token.getTokenType().getTokenType());
                } else {
                    aTOccWriterFile.print(token.getTokenValue());
                }
                aTOccWriterFile.print(" ");
            }

            // write token to the token file
            tokenWriterFile.print(token.getTokenType().getTokenType());
            tokenWriterFile.print(",");
            tokenWriterFile.print(token.getTokenValue());
            tokenWriterFile.print(",");
            tokenWriterFile.print(token.getLineNumber());
            tokenWriterFile.print(":");
            tokenWriterFile.print(token.getColumnNumber());
            tokenWriterFile.println();
        }
    }
}
