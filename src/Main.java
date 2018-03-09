import java.util.*;
import java.io.*;

public class Main {

    static HashMap<String, Integer> variables = new HashMap<String, Integer>();
    public static boolean checkSyntax = true; //devine false la var neinit
    public static boolean checkAssert = true; //devine false daca asertiunea e falsa
    public static boolean checkReturn = false; //am intalnit un return
    public static int returnValue = 0; //valoare de returnat

    /* Functie ce primeste un program de tip assignment, evalueaza expresia
     daca este posibil si introduce noua variabila in hashmap */
    public static void evalAssignment(String s) {
        s = s.replaceAll("\\[", "").replaceAll("\\]", "");
        String[] parts = s.split(" ");
        int n, op1, op2;
        // expresia din partea dreapta trebuie evaluata
        while (parts.length != 3) {
            n = parts.length;

            // verific daca am un simbol sau o valoare
            if (parts[n - 1].matches("^-?\\d+$")) {
                op1 = Integer.parseInt(parts[n - 1]);
            } else {
                // verific daca exista variabila a fost initializata inainte
                if (variables.containsKey(parts[n - 1])) {
                    op1 = variables.get(parts[n - 1]);
                } else {
                    checkSyntax = false;
                    return;
                }
            }

            // verific daca am un simbol sau o valoare
            if (parts[n - 2].matches("^-?\\d+$")) {
                op2 = Integer.parseInt(parts[n - 2]);
            } else {
                // verific daca exista variabila a fost initializata inainte
                if (variables.containsKey(parts[n - 2])) {
                    op2 = variables.get(parts[n - 2]);
                } else {
                    checkSyntax = false;
                    return;
                }
            }

            // aplic operatia intre simboluri/valori
            if (parts[n - 3].equals("+")) {
                parts[n - 3] = Integer.toString(op1 + op2);
            }
            if (parts[n - 3].equals("*")) {
                parts[n - 3] = Integer.toString(op1 * op2);
            }

            // dupa evaluare elimin ultimele doua elemente
            String[] aux = new String[n - 2];
            System.arraycopy(parts, 0, aux, 0, n - 2);
            parts = aux;
        }

        // variabila este adaugata in hashmap
        if (parts[2].matches("^-?\\d+$")) {
            variables.put(parts[1], Integer.parseInt(parts[2]));
        } else {
            if (variables.containsKey(parts[2])) {
                variables.put(parts[1], variables.get(parts[2]));
            } else {
                checkSyntax = false;
            }
        }
    }

    /* Functie ce primeste o expresie de tip boolean, o evalueaza
     si returneaza dupa caz true/false */
    public static boolean evalCondition(String s) {
        s = s.replaceAll("\\[", "").replaceAll("\\]", "");
        String[] parts = s.split(" ");
        int op1, op2, n;
        // expresia din partea dreapta trebuie evaluata
        while (parts.length != 3) {
            n = parts.length;
            // verific daca am un simbol sau o valoare
            if (parts[n - 1].matches("^-?\\d+$")) {
                op1 = Integer.parseInt(parts[n - 1]);
            } else {
                // verific daca exista variabila a fost initializata inainte
                if (variables.containsKey(parts[n - 1])) {
                    op1 = variables.get(parts[n - 1]);
                } else {
                    checkSyntax = false;
                    return false;
                }
            }

            // verific daca am un simbol sau o valoare
            if (parts[n - 2].matches("^-?\\d+$")) {
                op2 = Integer.parseInt(parts[n - 2]);
            } else {
                // verific daca exista variabila a fost initializata inainte
                if (variables.containsKey(parts[n - 2])) {
                    op2 = variables.get(parts[n - 2]);
                } else {
                    checkSyntax = false;
                    return false;
                }
            }

            // aplic operatia intre simboluri/valori
            if (parts[n - 3].equals("+")) {
                parts[n - 3] = Integer.toString(op1 + op2);
            }
            if (parts[n - 3].equals("*")) {
                parts[n - 3] = Integer.toString(op1 * op2);
            }

            // dupa evaluare elimin ultimele doua elemente
            String[] aux = new String[n - 2];
            System.arraycopy(parts, 0, aux, 0, n - 2);
            parts = aux;
        }

        //verificare valoare sau variabila
        if (parts[1].matches("^-?\\d+$")) {
            op1 = Integer.parseInt(parts[1]);
        } else {
            // verific daca exista variabila a fost initializata inainte
            if (variables.containsKey(parts[1])) {
                op1 = variables.get(parts[1]);
            } else {
                checkSyntax = false;
                return false;
            }
        }

        //verificare valoare sau variabila
        if (parts[2].matches("^-?\\d+$")) {
            op2 = Integer.parseInt(parts[2]);
        } else {
            // verific daca exista variabila a fost initializata inainte
            if (variables.containsKey(parts[2])) {
                op2 = variables.get(parts[2]);
            } else {
                checkSyntax = false;
                return false;
            }
        }

        //returnez true/false dupa cum este evaluata expresia
        if (parts[0].equals("==")) {
            return op1 == op2;
        }

        if (parts[0].equals("<")) {
            return op1 < op2;
        }

        return false;
    }

    /* Functie ce primeste un program de tip if si executa
     prog1 sau prog2 in functie de valarea returnata de conditie */
    public static void evalIf(String s) {
        String condition = null;
        String prog1 = null;
        String prog2 = null;
        int start = 1, inside = 0;

        // if-ul este impartit in conditie, program then si program else
        for (int i = 1; i < s.length() - 1; i++) {
            if (s.charAt(i) == '[') {
                if (inside == 0) {
                    start = i;
                }
                inside++;
            }

            if (s.charAt(i) == ']') {
                inside--;
                if (inside == 0) {
                    if (condition == null) {
                        condition = s.substring(start, i + 1);
                    } else if (prog1 == null) {
                        prog1 = s.substring(start, i + 1);
                    } else {
                        prog2 = s.substring(start, i + 1);
                    }
                }
            }
        }

        // programul pentru then/else este evaluat
        if (evalCondition(condition) == true) {
            evalProgram(prog1);
        } else {
            evalProgram(prog2);
        }
    }

    /* Functie ce primeste un program de tip for si executa
     prog1 cat timp conditia este true */
    public static void evalFor(String s) {
        String assign1 = null;
        String condition = null;
        String assign2 = null;
        String prog = null;
        int start = 1, inside = 0;

        // for-ul este impartit in assign1, conditie, assign2 si program
        for (int i = 1; i < s.length() - 1; i++) {
            if (s.charAt(i) == '[') {
                if (inside == 0) {
                    start = i;
                }
                inside++;
            }

            if (s.charAt(i) == ']') {
                inside--;
                if (inside == 0) {
                    if (assign1 == null) {
                        assign1 = s.substring(start, i + 1);
                    } else if (condition == null) {
                        condition = s.substring(start, i + 1);
                    } else if (assign2 == null) {
                        assign2 = s.substring(start, i + 1);
                    } else {
                        prog = s.substring(start, i + 1);
                    }
                }
            }
        }

        // variabila este initializata, for-ul se executa cat timp conditia = true
        evalProgram(assign1);
        while (evalCondition(condition) == true) {
            //se executa programul si assign2
            evalProgram(prog);
            evalProgram(assign2);
        }
    }

    /* Functie ce primeste un program de tip assert, verifica conditia
     si returneaza true/false */
    public static boolean evalAssert(String s) {
        if (checkAssert == true) {
            return evalCondition(s.substring(8, s.length() - 1));
        }
        return false;
    }

    /* Functie ce primeste un program de tip return, evalueaza expresia
     primita ca argument si actualizeaza valoarea de return a prog */
    public static void evalReturn(String s) {
        s = s.replaceAll("\\[", "").replaceAll("\\]", "");
        String[] parts = s.split(" ");
        int op1, op2, n;
        // expresia din partea dreapta trebuie evaluata
        while (parts.length != 2) {
            n = parts.length;
            // verific daca am un simbol sau o valoare
            if (parts[n - 1].matches("^-?\\d+$")) {
                op1 = Integer.parseInt(parts[n - 1]);
            } else {
                // verific daca exista variabila a fost initializata inainte
                if (variables.containsKey(parts[n - 1])) {
                    op1 = variables.get(parts[n - 1]);
                } else {
                    checkSyntax = false;
                    return;
                }
            }

            // verific daca am un simbol sau o valoare
            if (parts[n - 2].matches("^-?\\d+$")) {
                op2 = Integer.parseInt(parts[n - 2]);
            } else {
                // verific daca exista variabila a fost initializata inainte
                if (variables.containsKey(parts[n - 2])) {
                    op2 = variables.get(parts[n - 2]);
                } else {
                    checkSyntax = false;
                    return;
                }
            }

            // aplic operatia intre simboluri/valori
            if (parts[n - 3].equals("+")) {
                parts[n - 3] = Integer.toString(op1 + op2);
            }
            if (parts[n - 3].equals("*")) {
                parts[n - 3] = Integer.toString(op1 * op2);
            }

            // dupa evaluare elimin ultimele doua elemente
            String[] aux = new String[n - 2];
            System.arraycopy(parts, 0, aux, 0, n - 2);
            parts = aux;
        }

        //variabila este pusa in returnValue si checkReturn devine true
        if (checkReturn == false) {
            if (parts[1].matches("^-?\\d+$")) {
                returnValue = Integer.parseInt(parts[1]);
            } else {
                if (variables.containsKey(parts[1])) {
                    returnValue = variables.get(parts[1]);
                } else {
                    checkSyntax = false;
                    return;
                }
            }
        }
        checkReturn = true;
    }

    /* Functie ce primeste un bloc program (;)
     si il imparte in "subprograme" */
    public static String[] splitProgram(String s) {
        List<String> l = new LinkedList<String>();
        int inside = 0;
        int start = 0, stop = 0;

        for (int i = 0; i < s.length(); i++) {
            // ma aflu in interiorul unei paranteze
            if (s.charAt(i) == '[') {
                inside++;
                stop++;
                continue;
            }
            // am gasit un subprogram
            if (s.charAt(i) == ';') {
                // ignor o paranteza
                if (inside > 0) {
                    inside--;
                }
                //aici incepe un subprogram
                if (inside == 0) {
                    start = i + 2;
                }
                stop++;
                continue;
            }
            // se inchide o paranteza
            if (s.charAt(i) == ']') {
                if (inside > 0) {
                    inside--;
                }
                stop++;
                continue;
            }
            // am gasit un subprogram si il adaug in lista
            if (s.charAt(i) == ' ' && inside == 0 && start < stop) {
                l.add(s.substring(start, stop));
                start = i + 1;
                stop = start;
                continue;
            }
            stop++;
        }
        //adaug ultimul subprog
        if (stop > start) {
            l.add(s.substring(start, stop));
        }

        String[] result = l.toArray(new String[l.size()]);
        int index = -1;

        //numar parantezele [ si ]
        for (String elem : result) {
            index++;
            int par = 0;
            for (int i = 0; i < elem.length(); i++) {
                if (elem.charAt(i) == '[') {
                    par++;
                }
                if (elem.charAt(i) == ']') {
                    par--;
                }
            }
            // elimin parantezele ] in plus
            if (par != 0) {
                par = Math.abs(par);
                elem = elem.substring(0, elem.length() - par);
                result[index] = elem;
            }
        }
        return result;
    }

    /* Functie ce primeste un string de tip program si in functie de
     acesta este folosita functia de evaluare specifica */
    public static void evalProgram(String s) {
        String[] list = splitProgram(s);

        for (String elem : list) {
            if (elem.charAt(1) == '=' && elem.charAt(2) == ' ') {
                evalAssignment(elem);
            }

            if (elem.charAt(1) == 'i') {
                evalIf(elem);
            }

            if (elem.charAt(1) == 'f') {
                evalFor(elem);
            }

            if (elem.charAt(1) == 'a') {
                checkAssert = evalAssert(elem);
            }

            if (elem.charAt(1) == 'r') {
                evalReturn(elem);
            }

            if (elem.charAt(1) == ';') {
                evalProgram(elem);
            }
        }
    }

    public static void main(String[] args) {
        try {
            FileReader in = new FileReader(args[0]);
            PrintWriter out = new PrintWriter(new FileWriter(args[1]));
            StringBuilder temp = new StringBuilder(1000000);

            //programul e citit caracter cu caracter din fisier
            int c;
            while ((c = in.read()) != -1) {
                temp.append((char) c);
            }

            //il convertesc la string si elimin tab, endline ...
            String program = temp.toString();
            program = program.replaceAll("(\\t|\\r|\\n|\\r\\n)+", " ");
            program = program.replaceAll("( ])+", "]");

            //evaluez programul si afisez in fisier
            evalProgram(program);
            if (checkSyntax == false) {
                out.print("Check failed");
            } else if (checkReturn == false) {
                out.print("Missing return");
            } else if (checkAssert == false) {
                out.print("Assert failed");
            } else {
                out.print(returnValue);
            }

            in.close();
            out.close();
        } catch (IOException e) {
        }
    }
}