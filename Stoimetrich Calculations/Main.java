import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Element[]> reactantHashMap = new HashMap<Integer, Element[]>();
        HashMap<Integer, Element[]> productHashMap = new HashMap<Integer, Element[]>();
        String string = "P4O10+H2O=H3PO4";
        string = "C7H16+O2=CO2+H2O";
        string = "Al2(SO4)3+Ca(OH)2=Al(OH)3+CaSO4";
        String[] reactants = string.split("=");
        String[] products = reactants[1].split("[+]");
        reactants = reactants[0].split("[+]");
        saveElements(reactants, reactantHashMap);
        saveElements(products, productHashMap);
        int[] array = balanceEquations(reactantHashMap, productHashMap);
        for (int i : array) {
            System.out.println(i);
        }
    }

    public static int[] balanceEquations(HashMap<Integer, Element[]> reactantHashMap, HashMap<Integer, Element[]> productHashMap) {
        int[] array = new int[reactantHashMap.size() + productHashMap.size()];
        for (int pos = 0; pos < array.length; pos++) {
            array[pos] = 1;
        }
        for (; ; ) {
            if (checkBalance(reactantHashMap, productHashMap, array)) {
                return array;
            }
            int pos = 0;
            array[pos]++;
            while (array[pos] > 50) {
                array[pos] = 1;
                pos++;
                array[pos]++;
            }
        }
    }
    public static boolean checkBalance(HashMap<Integer, Element[]> reactantHashMap, HashMap<Integer, Element[]> productHashMap, int[] array) {
        Main m = new Main();
        HashMap<String, Integer> tempreactantHashMap = new HashMap<String, Integer>();
        HashMap<String, Integer> tempproductHashMap = new HashMap<String, Integer>();
        int pos = 0;
        while (pos < reactantHashMap.size()) {
            HashMap<String, Integer> tempHashMap = convertArray(reactantHashMap.get(pos), array[pos]);
            m.merge(tempreactantHashMap, tempHashMap);
            pos++;
        }
        int max = pos;
        while (pos - max < productHashMap.size()) {
            HashMap<String, Integer> tempHashMap = convertArray(productHashMap.get(pos-max), array[pos]);
            m.merge(tempproductHashMap, tempHashMap);
            pos++;
        }
        for (Map.Entry<String, Integer> elem : tempreactantHashMap.entrySet()) {
            if (!(tempproductHashMap.containsKey(elem.getKey()) && tempproductHashMap.get(elem.getKey()).equals(elem.getValue()))) {
                return false;
            }
        }
        return true;
    }

    public static HashMap<String, Integer> convertArray(Element[] array, int multiply) {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        for (Element pos : array) {
            hashMap.put(pos.name, pos.amount * multiply);
        }
        return hashMap;
    }

    public static HashMap<Integer, Element[]> saveElements(String[] input, HashMap<Integer, Element[]> output) {
        Main m = new Main();
        for (String i : input) {
            HashMap<String, Integer> hashMap = m.parseCompound(i);
            Element[] array = new Element[hashMap.size()];
            int pos = 0;
            for (Map.Entry<String, Integer> elem : hashMap.entrySet()) {
                String key = elem.getKey();
                int value = (int) elem.getValue();
                Element element = m.new Element();
                element.name = key;
                element.amount = value;
                array[pos] = element;
                pos++;
            }
            output.put(output.size(), array);
        }
        return output;
    }

    class Token {
        public int type = Unknown;
        public String value = null;

        final static public int Unknown = -1;
        final static public int Number = 0;
        final static public int Element = 1;
        final static public int LeftParan = 2;
        final static public int RightParan = 3;
    }

    class Element {
        private static final int Unknown = 0;
        public String name = null;
        public int amount = Unknown;
    }

    class TokenReader {
        public TokenReader(String s) {
            this.content = s;
        }

        public void putBack(Token t) {
            content = t.value + content;
        }

        public Token getNext() {
            if (content.isEmpty()) {
                return null;
            }

            if (content.charAt(0) >= '0' && content.charAt(0) <= '9') {
                int j = 1;
                for (; j < content.length(); ++j) {
                    if (!(content.charAt(j) >= '0' && content.charAt(j) <= '9')) {
                        break;
                    }
                }
                Token t = new Token();
                t.type = Token.Number;
                t.value = content.substring(0, j);
                content = content.substring(j);
                return t;
            }

            if (content.charAt(0) >= 'A' && content.charAt(0) <= 'Z') {
                int j = 1;
                for (; j < content.length(); ++j) {
                    if (!(content.charAt(j) >= 'a' && content.charAt(j) <= 'z')) {
                        break;
                    }
                }
                Token t = new Token();
                t.type = Token.Element;
                t.value = content.substring(0, j);
                content = content.substring(j);
                return t;
            }

            if (content.charAt(0) == '(') {
                Token t = new Token();
                t.type = Token.LeftParan;
                t.value = "(";
                content = content.substring(1);
                return t;
            }

            if (content.charAt(0) == ')') {
                Token t = new Token();
                t.type = Token.RightParan;
                t.value = ")";
                content = content.substring(1);
                return t;
            }

            return null;
        }

        private String content = null;
    }

    // merge b to a
    void merge(HashMap<String, Integer> a, HashMap<String, Integer> b) {
        for (Map.Entry<String, Integer> elem : b.entrySet()) {
            String key = (String) elem.getKey();
            int value = (int) elem.getValue();
            if (!a.containsKey(key)) {
                a.put(key, value);
            } else {
                a.put(key, (int) a.get(key) + value);
            }
        }
    }
    void multiply(HashMap<String, Integer> a, int num) {
        for (Map.Entry<String, Integer> elem : a.entrySet()) {
            String key = (String) elem.getKey();
            int value = (int) elem.getValue();
            a.put(key, value*num);
        }
    }

    HashMap<String, Integer> parseCompound(String s) {
        HashMap<String, Integer> currentContext = new HashMap<String, Integer>();
        TokenReader tr = new TokenReader(s);
        Stack<HashMap<String, Integer>> stack = new Stack<HashMap<String, Integer>>();
        while (true) {
            Token t = tr.getNext();
            if (t == null) {
                break;
            }
            if (t.type == Token.Element) {
                int num = 1;
                Token nxt = tr.getNext();
                if (nxt != null) {
                    if (nxt.type == Token.Number) {
                        num = Integer.valueOf(nxt.value);
                    } else {
                        tr.putBack(nxt);
                    }
                }
                currentContext.put(t.value, num);
            } else if (t.type == Token.LeftParan) {
                stack.push(currentContext);
                currentContext = new HashMap<String, Integer>();
            } else if (t.type == Token.RightParan) {
                HashMap<String, Integer> previous = stack.pop();
                int num = 1;
                Token nxt = tr.getNext();
                if (nxt != null) {
                    if (nxt.type == Token.Number) {
                        num = Integer.valueOf(nxt.value);
                    } else {
                        tr.putBack(nxt);
                    }
                }
                multiply(currentContext, num);
                merge(currentContext, previous);
            }
        }
        return currentContext;
    }
}