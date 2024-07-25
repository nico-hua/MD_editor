package org.example.text;

public class TextFactory {
    //根据传入的字符串创建相应的Text
    public Text createText(String textStr){
        String[] parts=textStr.split(" ",2);
        if(parts.length<2){
            throw new RuntimeException("Invalid Text!");
        }
        String textType=parts[0];
        if(textType.startsWith("#")){
            int level=textType.split("#",-1).length-1;
            if(level>6){
                throw new RuntimeException("Invalid Text!");
            }
            String content=parts[1];
            return new Header(level,content);
        } else if (textType.matches("\\d*\\.$")) {
            String content=parts[1];
            return new OrderedText(Integer.parseInt(textType.replaceFirst("\\.","")),content);
        } else if (textType.equals("*")||textType.equals("+")||textType.equals("-")) {
            String content=parts[1];
            return new UnorderedText(textType.charAt(0),content);
        } else {
            throw new RuntimeException("Invalid Text!");
        }
    }

}
