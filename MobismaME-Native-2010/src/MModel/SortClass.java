package MModel;

import java.io.IOException;

import javax.microedition.rms.*;

import MDataStore.DataBase_RMS;

/**
 * <p>Title: Mobile Extesion</p>
 *
 * <p>Description: All PBX Include</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: mobisma ab</p>
 *
 * @author Peter Albertsson
 * @version 2.0
 */
public class SortClass {


    // Vector f�r str�ngar.
    private String[] subStr;

    public String

    switchBoardNumber_PBX,
    setP_PBX,
    lineAccess_PBX,
    countryCode_PBX,
    call_This_Number = "";



    public SortClass() {

        DataBase_RMS rms = null;
        try {
            rms = new DataBase_RMS();
        } catch (IOException ex) {
        } catch (RecordStoreNotOpenException ex) {
        } catch (InvalidRecordIDException ex) {
        } catch (RecordStoreException ex) {
        }

        try {
            this.lineAccess_PBX = rms.getAccessNumber();
        } catch (RecordStoreNotOpenException ex1) {
        } catch (InvalidRecordIDException ex1) {
        } catch (RecordStoreException ex1) {
        }
        try {
            this.countryCode_PBX = rms.getCountryCode();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }
        try {
            this.switchBoardNumber_PBX = rms.getSwitchBoardNumber();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }
        try {
            this.setP_PBX = rms.getDTMFsend();
        } catch (RecordStoreNotOpenException ex2) {
        } catch (InvalidRecordIDException ex2) {
        } catch (RecordStoreException ex2) {
        }


        rms = null;
    }

    // Justerar landsiffra som �r inmatad! Tar bort '+' och l�gger in '00' f�re landssiffran
    public String checkCallNumber(String checkCallString) {

        MModel.InternNumber intern = new InternNumber();

            DataBase_RMS rms = null;
            try {
                rms = new DataBase_RMS();
            } catch (IOException ex1) {
            } catch (RecordStoreNotOpenException ex1) {
            } catch (InvalidRecordIDException ex1) {
            } catch (RecordStoreException ex1) {
            }


        String plus = "+";
        String setNumberTo_00 = "00";
        String validateCallString = checkCallString;
        String validateCountryCode = this.countryCode_PBX;
        String setNumberTo_0 = "0";
        String callString = "";

        // --- kontrollerar om egna landsnummret best�r av '0046' g�r om till '0'
        // --- Om '00' �r lika med tv� f�rsta tecknen OCH landsnumret �r lika med '46'
        if (setNumberTo_00.equals(validateCallString.substring(0, 2)) &&
            validateCountryCode.equals(validateCallString.substring(2, 4))) {

            String setString = validateCallString;
            // ta bort plast 0 - 3 ur str�ngen....
            String deletePartOfString = setString.substring(4);
            // s�tt ihop str�ngen setStringTotal
            try {
                if (rms.getAccessNumber().equals("NONE")) {

                    this.lineAccess_PBX = "";

                }
            } catch (RecordStoreNotOpenException ex2) {
            } catch (InvalidRecordIDException ex2) {
            } catch (RecordStoreException ex2) {
            }
            String setStringTotal = lineAccess_PBX + setNumberTo_0 + deletePartOfString;

            String setCalledNumber = setNumberTo_0 + deletePartOfString;
            try {
                intern.replaceDialledNumber(setCalledNumber);
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            } catch (IOException ex) {
            }

            callString = setStringTotal;
            // 00468293574 == 08293574 tex.

            return callString;

        }

        // --- kontrollerar om egna landsnummret best�r av '00358' g�r om till '0'
        // --- Om '00' �r lika med tv� f�rsta tecknen OCH landsnumret �r lika med '358'
        else if (setNumberTo_00.equals(validateCallString.substring(0, 2)) &&
                 validateCountryCode.equals(validateCallString.substring(2, 5))) {

            String setString = validateCallString;
            // ta bort plast 0 - 3 ur str�ngen....
            String deletePartOfString = setString.substring(5);
            // s�tt ihop str�ngen setStringTotal
            try {
                if (rms.getAccessNumber().equals("NONE")) {

                    this.lineAccess_PBX = "";

                }
            } catch (RecordStoreNotOpenException ex2) {
            } catch (InvalidRecordIDException ex2) {
            } catch (RecordStoreException ex2) {
            }

            String setStringTotal = lineAccess_PBX + setNumberTo_0 + deletePartOfString;

            String setCalledNumber = setNumberTo_0 + deletePartOfString;
            try {
                intern.replaceDialledNumber(setCalledNumber);
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            } catch (IOException ex) {
            }


            callString = setStringTotal;
            // 003588293574 == 08293574 tex.
            return callString;

        }

        // Om numret inneh�ller 1 - 5 siffror s� �r det ett internnummer.
        if (validateCallString.equals(validateCallString.substring(0, 1)) ||
            validateCallString.equals(validateCallString.substring(0, 2)) ||
            validateCallString.equals(validateCallString.substring(0, 3)) ||
            validateCallString.equals(validateCallString.substring(0, 4)) ||
            validateCallString.equals(validateCallString.substring(0, 5))) {

            callString = validateCallString;

            String setCalledNumber = validateCallString;
            try {
                intern.replaceDialledNumber(setCalledNumber);
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            } catch (IOException ex) {
            }


            return callString;

        }
        // Om numret startar med '+' OCH '358' �r sann s� g�r om till '0'.
        if (plus.equals(validateCallString.substring(0, 1)) &&
            validateCountryCode.equals(validateCallString.substring(1, 4))) { // Om 358 == 358

            String setString = validateCallString;

            String deletePartOfString = setString.substring(4); // ta bort plast 0 - 3 ur str�ngen....

            try {
                if (rms.getAccessNumber().equals("NONE")) {

                    this.lineAccess_PBX = "";

                }
            } catch (RecordStoreNotOpenException ex2) {
            } catch (InvalidRecordIDException ex2) {
            } catch (RecordStoreException ex2) {
            }

            String setStringTotal = lineAccess_PBX + setNumberTo_0 + deletePartOfString;

            String setCalledNumber = setNumberTo_0 + deletePartOfString;
            try {
                intern.replaceDialledNumber(setCalledNumber);
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            } catch (IOException ex) {
            }


            callString = setStringTotal;

            return callString;

        }

        // Om numret startar med '+' OCH countryCode �r lika med '46' s� g�r om till '0'.
        else if (plus.equals(validateCallString.substring(0, 1)) &&
                 validateCountryCode.equals(validateCallString.substring(1, 3))) {


            String setString = validateCallString;

            String deletePartOfString = setString.substring(3); // ta bort plast 0 - 3 ur str�ngen....

            try {
                if (rms.getAccessNumber().equals("NONE")) {

                    this.lineAccess_PBX = "";

                }
            } catch (RecordStoreNotOpenException ex2) {
            } catch (InvalidRecordIDException ex2) {
            } catch (RecordStoreException ex2) {
            }

            String setStringTotal = lineAccess_PBX + setNumberTo_0 + deletePartOfString;

            String setCalledNumber = setNumberTo_0 + deletePartOfString;
            try {
                intern.replaceDialledNumber(setCalledNumber);
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            } catch (IOException ex) {
            }


            callString = setStringTotal;

            return callString;

        }

        // Om numret startar med '+' OCH countryCode �r INTE lika med '46' s� g�r om till '0047'.
        else if (plus.equals(validateCallString.substring(0, 1)) &&
                 !validateCountryCode.equals(validateCallString.substring(1, 3))) {

            String setString = validateCallString;

            String deletePartOfString = setString.substring(1); // ta bort plats 0 - 1 ur str�ngen....

            try {
                if (rms.getAccessNumber().equals("NONE")) {

                    this.lineAccess_PBX = "";

                }
            } catch (RecordStoreNotOpenException ex2) {
            } catch (InvalidRecordIDException ex2) {
            } catch (RecordStoreException ex2) {
            }

            String setStringTotal = lineAccess_PBX + setNumberTo_00 + deletePartOfString;

            String setCalledNumber = setNumberTo_00 + deletePartOfString;
            try {
                intern.replaceDialledNumber(setCalledNumber);
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            } catch (IOException ex) {
            }


            callString = setStringTotal;

            return callString;

        }
        // Om numret inte liknar '+' s� �r det riktnummer + telefonnummer.
        if (!plus.equals(validateCallString.substring(0, 1))) {

            try {
                if (rms.getAccessNumber().equals("NONE")) {

                    this.lineAccess_PBX = "";

                }
            } catch (RecordStoreNotOpenException ex2) {
            } catch (InvalidRecordIDException ex2) {
            } catch (RecordStoreException ex2) {
            }

            callString = lineAccess_PBX + validateCallString;

            String setCalledNumber = validateCallString;
            try {
                intern.replaceDialledNumber(setCalledNumber);
            } catch (InvalidRecordIDException ex) {
            } catch (RecordStoreNotOpenException ex) {
            } catch (RecordStoreException ex) {
            } catch (IOException ex) {
            }

            return callString;

        }
        rms = null;
        intern = null;
        return callString;
    }


    // Metoden sorterar bort tecken och visar siffror
    public String sortCharToDigits(String s) {

        String sString = s;

        StringBuffer bTecken = new StringBuffer(sString); // L�gg str�ngen sortString i ett stringbuffer objekt...

        for (int i = 0; i < bTecken.length(); i++) { // r�kna upp hela bTecken-str�ngens inneh�ll hela dess l�ngd

            char tecken = bTecken.charAt(i); // char tecken �r inneh�llet i hela l�ngden

            /* Tecknen t�cker >> Tyska, Dutch, Svenska, Engelska, Norska, Danska.
                Spanska, Italenska, Finska, Franska */

            if ('A' <= tecken && tecken <= 'Z' ||
                'a' <= tecken && tecken <= 'z'
                || tecken == '-' || tecken == '/' || tecken == '\\' ||
                tecken == ':' || tecken == ';'
                || tecken == '.' || tecken == ',' || tecken == '_' ||
                tecken == '|' || tecken == '<'
                || tecken == '>' || tecken == '+' || tecken == '(' ||
                tecken == ')' || tecken == '�' || tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�' || tecken == '�' || tecken == '�' ||
                tecken == '�') {

                bTecken.setCharAt(i, ' '); // l�gg in blanksteg i str�ngen d�r n�got av ovanst�ende tecken finns....
            }

        }

        bTecken.append(' '); // l�gger till blanksteg sist i raden s� att sista kommer med f�r att do-satsen ska kunna hitta och sortera...

        String setString = new String(bTecken); // G�r om char-str�ngen till en string-str�ng

        int antal = 0;
        char separator = ' '; // f�r att kunna sortera i do-satsen

        int index = 0;

        do { // do satsen sorterar ut blankstegen och g�r en ny str�ng f�r att j�mf�ra IMEI med...
            ++antal;
            ++index;

            index = setString.indexOf(separator, index);
        } while (index != -1);

        subStr = new String[antal];
        index = 0;
        int slutindex = 0;

        for (int j = 0; j < antal; j++) {

            slutindex = setString.indexOf(separator, index);

            if (slutindex == -1) {
                subStr[j] = setString.substring(index);
            }

            else {
                subStr[j] = setString.substring(index, slutindex);
            }

            index = slutindex + 1;

        }
        String setNumber = "";
        for (int k = 0; k < subStr.length; k++) {

            setNumber += subStr[k]; // L�gg in v�rdena fr�n subStr[k] i str�ngen setNumber....
        }

        // System.out.println("Sorterad: " + setNumber);

        String sortedString = setNumber;

        return sortedString;
    }

    // Metoden sorterar bort siffror och 'vissa tecken'
    public String sortDigitsToCharacter(String s) {

        String sString = s;

        StringBuffer bTecken = new StringBuffer(sString); // L�gg str�ngen sortString i ett stringbuffer objekt...

        for (int i = 0; i < bTecken.length(); i++) { // r�kna upp hela bTecken-str�ngens inneh�ll hela dess l�ngd

            char tecken = bTecken.charAt(i); // char tecken �r inneh�llet i hela l�ngden

            // Sorterar bort f�ljande tecken.
            if (tecken == '-' || tecken == '/' || tecken == '\\' ||
                tecken == ':' || tecken == ';'
                || tecken == '.' || tecken == ',' || tecken == '_' ||
                tecken == '|' || tecken == '<'
                || tecken == '>' || tecken == '+' || tecken == '(' ||
                tecken == ')' || tecken == '0' || tecken == '1' ||
                tecken == '2' || tecken == '3' || tecken == '4' ||
                tecken == '5' ||
                tecken == '6' || tecken == '7' || tecken == '8' ||
                tecken == '9' || tecken == '*' || tecken == '#') {

                bTecken.setCharAt(i, ' '); // l�gg in blanksteg i str�ngen d�r n�got av ovanst�ende tecken finns....
            }

        }

        bTecken.append(' '); // l�gger till blanksteg sist i raden s� att sista kommer med f�r att do-satsen ska kunna hitta och sortera...

        String setString = new String(bTecken); // G�r om char-str�ngen till en string-str�ng

        int antal = 0;
        char separator = '*'; // f�r att kunna sortera i do-satsen

        int index = 0;

        do { // do satsen sorterar ut blankstegen och g�r en ny str�ng f�r att j�mf�ra IMEI med...
            ++antal;
            ++index;

            index = setString.indexOf(separator, index);
        } while (index != -1);

        subStr = new String[antal];
        index = 0;
        int slutindex = 0;

        for (int j = 0; j < antal; j++) {

            slutindex = setString.indexOf(separator, index);

            if (slutindex == -1) {
                subStr[j] = setString.substring(index);
            }

            else {
                subStr[j] = setString.substring(index, slutindex);
            }

            index = slutindex + 1;

        }
        String setNumber = "";
        for (int k = 0; k < subStr.length; k++) {

            setNumber += subStr[k]; // L�gg in v�rdena fr�n subStr[k] i str�ngen setNumber....
        }

        // System.out.println("Sorterad: " + setNumber);

        String sortedString = setNumber;

        return sortedString;
    }


}
