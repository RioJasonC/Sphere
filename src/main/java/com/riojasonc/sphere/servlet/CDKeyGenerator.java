package com.riojasonc.sphere.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Calendar;

@WebServlet(name = "CDKeyGenerator", urlPatterns = "/CDKeyGenerator")
public class CDKeyGenerator extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Calendar calendar = Calendar.getInstance();
        int a = calendar.get(Calendar.YEAR), b = calendar.get(Calendar.MONTH) + 1, c = calendar.get(Calendar.DAY_OF_MONTH);
        int d = calendar.get(Calendar.HOUR_OF_DAY), e = calendar.get(Calendar.MINUTE), f = calendar.get(Calendar.SECOND);
        int g = calendar.get(Calendar.DAY_OF_WEEK);
        a = ((((f * 100 + b) * 100 + c) * 100 + d) * 10000 + a) * 100 + e;
        b = (((f * 100 + c) * 100 + d) * 100 + e) * 100 + f;
        c = (((f * 100 + d) * 100 + e) * 100 + f) * 10 + g;
        char[] assiic = {'2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        BitSet A = bitsetCreate(a);
        BitSet B = bitsetCreate(b);
        BitSet C = bitsetCreate(c);
        BitSet bitstring = new BitSet(48);
        BitSet ascii = new BitSet(5);
        BitSet submit;

        int pos = 32, sum = 0;
        for(int i = 0; i < 16; i++){
            bitstring.set(i, A.get(i));
            bitstring.set(i + 16, B.get(i));
            bitstring.set(i + 32, C.get(i));
        }

        String cdkey = "";

        for(int i = 1; i <= 14; i++, pos++){
            for(int j = 0;j < 5; j++){
                ascii.set(j, bitstring.get((j + pos) % 48));
            }
            cdkey += assiic[booleanToInt(ascii.get(0)) + 2 * booleanToInt(ascii.get(1)) + 4 * booleanToInt(ascii.get(2)) + 8 * booleanToInt(ascii.get(3)) + 16 * booleanToInt(ascii.get(4))];
            sum = assiic[booleanToInt(ascii.get(0)) + 2 * booleanToInt(ascii.get(1)) + 4 * booleanToInt(ascii.get(2)) + 8 * booleanToInt(ascii.get(3)) + 16 * booleanToInt(ascii.get(4))];
            if(i % 4 == 0){
                cdkey += "-";
            }
        }

        submit = bitsetCreate(sum);
        cdkey += assiic[booleanToInt(submit.get(0)) + 2 * booleanToInt(submit.get(1)) + 4 * booleanToInt(submit.get(2)) + 8 * booleanToInt(submit.get(3)) + 16 * booleanToInt(submit.get(4))];
        cdkey += assiic[booleanToInt(submit.get(5)) + 2 * booleanToInt(submit.get(6)) + 4 * booleanToInt(submit.get(7)) + 8 * booleanToInt(submit.get(8)) + 16 * booleanToInt(submit.get(9))];

        PrintWriter pw = response.getWriter();
        pw.print(cdkey);
    }

    private static BitSet bitsetCreate(int i){
        BitSet result = new BitSet(32);
        int pos = 0;
        while(i > 0){
            if(i % 2 == 1){
                result.set(pos);
            }
            pos++;
            i /= 2;
        }
        return result;
    }

    private static int booleanToInt(boolean b){
        return b ? 1 : 0;
    }
}
