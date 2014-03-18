/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * 
 * Implementação de um java.nio.charset.Charset para possibilitar a conversão
 * bidirecional entre Byte[] e String.
 * 
 * @author antunes
 */
public class ByteCharset extends Charset {

    public ByteCharset() {
        super("ByteCharset", null);
    }

    public class Encoder extends CharsetEncoder {

        public Encoder(Charset c) {
            super(c, 1, 1);
        }

        @Override
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            int mark = in.position();
            while (in.hasRemaining()) {
                if (out.hasRemaining()) {
                    out.put((byte) in.get());
                } else {
                    return CoderResult.OVERFLOW;
                }
                mark++;
            }
            in.position(mark);
            return CoderResult.UNDERFLOW;
        }
    }

    public class Decoder extends CharsetDecoder {

        public Decoder(Charset c) {
            super(c, 1, 1);
        }

        @Override
        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            int mark = in.position();
            while (in.hasRemaining()) {
                if (out.hasRemaining()) {
                    out.put((char) in.get());
                } else {
                    return CoderResult.OVERFLOW;
                }
                mark++;
            }
            in.position(mark);
            return CoderResult.UNDERFLOW;
        }
    }

    @Override
    public boolean canEncode() {
        return true;
    }

    @Override
    public boolean contains(Charset cs) {
        return false;
    }

    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }

    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    public static void findApropriateStringConversion (){
        //cria um vetor de bytes com todos os valores possiveis
        byte[] values  = new byte[256];
        int c = 0;
        for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++){
            values[c] = b;
            c++;
        }
        boolean isValidConversion = false;
        //testa todos os charsets disponiveis
        for (Charset charset : Charset.availableCharsets().values()){
            //verifica se é possivel codificar (criar uma string) com o charset atual
            if (charset.canEncode()){
                isValidConversion = true;
                //converte em uma string com o charset
                String str = new String(values, charset);
                //converte de volta em um vetor de bytes
                byte [] newValues = str.getBytes(charset);
                //compara os dois vetores
                for (int i = 0; i < values.length && i < newValues.length; i++){
                    if (values[i] != newValues[i]){
                        //charset invalido
                        isValidConversion = false;
                        break;
                    }
                }
//                if (isValidConversion){
//                    System.out.println(str);
//                }
                
            }
            if (isValidConversion){
                System.out.println(charset.name());
            }
        }
    }
    
    
    public static boolean isValid(Charset cs) {
        if (!cs.canEncode()) {
            return false;
        }

        ByteBuffer bb = ByteBuffer.allocate(2);
        for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
            bb.rewind();
            bb.put(b);
            CharBuffer cb;
            try {
                // byte -> char
                cb = cs.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .decode(bb);
                //char -> byte
                ByteBuffer bbe = cs.newEncoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .encode(cb);

                if (bb.hasArray()
                        && bbe.hasArray()
                        && bb.array().length > 0
                        && bbe.array().length > 0) {
                    if (bb.array()[0] != bbe.array()[0]) {
                        return false;
                    }
                }
            } catch (CharacterCodingException ex) {
                System.out.println(ex);
                return false;
            }
        }
        return true;
    }
    
    
}
