/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kavprot;

/**
 *
 * @author arsslen
 */
public class EncodingProvider {
    
public static String EncodeB64(byte[] data)
{
   return new String(Base64.encode(data));
}

public static byte[] DecodeB64(String data)
{
return Base64.decode(data);
}
    
public static String EncodeB16(byte[] data)
{
   return new String(Hex.encode(data));
}

public static byte[] DecodeB16(String data)
{
return Hex.decode(data);
}
}
