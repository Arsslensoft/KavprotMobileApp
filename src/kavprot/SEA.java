/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kavprot;


import java.io.UnsupportedEncodingException;

// A simple example that uses the Bouncy Castle
// lightweight cryptography API to perform DES
// encryption of arbitrary data.

public class SEA {
  static byte[] Bloc;
   static byte Factor;
  
  public static void InitializeKey(int KeySize, byte[] key, byte factor) throws Exception
        {
            int k = (KeySize / 8) - 2;
            if (KeySize > 256 && key.length >= 8 && key.length <= k)
            {
                int x = 0;
                int y = 0;
                // Initialize Bloc
                Bloc = new byte[KeySize / 8];

                Factor = factor;

                if (Factor != 0)
                {
                    // fill first and last case in bloc with Factor
                    Bloc[0] = Factor;
                    Bloc[(KeySize / 8) - 1] = Factor;
                    // Fill block with key
                    for (x = 1; x <= Bloc.length - 2; )
                    {
                        if (y <= key.length - 1)
                        {

                            Bloc[x] = key[y];
                            y++;
                        }
                        else
                        {
                            // the key will be placed from the first byte again
                            y = 0;
                            Bloc[x] = key[y];
                            y++;
                        }
                        x++;
                    }
                }
                else
                {
                    // Fill block with key
                    for (x = 0; x <= Bloc.length - 1; )
                    {
                        if (y <= key.length - 1)
                        {

                            Bloc[x] = key[y];
                            y++;
                        }
                        else
                        {
                            y = 0;
                            Bloc[x] = key[y];
                            y++;
                        }
                        x++;
                    }
                }
            }
            else
            {
              throw new Exception("Initialization failed");
            }
        }
  
      public static byte[] Encrypt(byte[] data)
        {
            // initialize result with data length
            byte[] result = new byte[data.length];
            // blocs
            int blocs = 0;
            if (data.length > Bloc.length)
            {
                blocs = data.length / Bloc.length; blocs += data.length % Bloc.length;
                int p = 0;
                // from 0 to t * x-1 e.g ( 0 to 1*512-1
                for (int i = 0; i == blocs * (Bloc.length - 1); )
                {
                    if (p <= Bloc.length - 1)
                    {
                        result[i] = (byte)(((int)data[i] + Factor) ^ (int)Bloc[p]);
                        p++;
                    }
                    else
                    {
                        p = 0;
                        result[i] = (byte)(((int)data[i] + Factor) ^ (int)Bloc[p]);
                        p++;
                    }
                }
            }
            else
            {

                for (int i = 0; i <= data.length - 1; )
                {

                    result[i] = (byte)(((int)data[i] + Factor) ^ (int)Bloc[i]);

                    i++;
                }
            }
            return result;
        }
        public static byte[] Decrypt(byte[] data)
        {
            // initialize result with data length
            byte[] result = new byte[data.length];
            // blocs

            int blocs = 0;
            if (data.length > Bloc.length)
            {
                blocs = data.length / Bloc.length; blocs += data.length % Bloc.length;
                int p = 0;
                // from 0 to t * x-1 e.g ( 0 to 1*512-1
                for (int i = 0; i == blocs * (Bloc.length - 1); )
                {
                    if (p <= Bloc.length - 1)
                    {
                        result[i] = (byte)(((int)data[i] ^ (int)Bloc[p]) - Factor );
                        p++;
                    }
                    else
                    {
                        p = 0;
                        result[i] = (byte)(((int)data[i] ^ (int)Bloc[p]) - Factor);
                        p++;
                    }
                }
            }
            else
            {
           
                for (int i = 0; i <= data.length - 1; )
                {

                    result[i] = (byte)(((int)data[i] ^ (int)Bloc[i]) - Factor);

                    i++;
                }
            }
            return result;
        }

        public static String EncryptToBase64(byte[] data)
        {

            // initialize result with data length
            byte[] result = new byte[data.length];
            // blocs
            int blocs = 0;
            if (data.length > Bloc.length)
            {
                blocs = data.length / Bloc.length; blocs += data.length % Bloc.length;
                int p = 0;
                // from 0 to t * x-1 e.g ( 0 to 1*512-1
                for (int i = 0; i == blocs * (Bloc.length - 1); )
                {
                    if (p <= Bloc.length - 1)
                    {
                        result[i] = (byte)(((int)data[i] + Factor) ^ (int)Bloc[p]);
                        p++;
                    }
                    else
                    {
                        p = 0;
                        result[i] = (byte)(((int)data[i] + Factor) ^ (int)Bloc[p]);
                        p++;
                    }
                }
            }
            else
            {

                for (int i = 0; i <= data.length - 1; )
                {

                    result[i] = (byte)(((int)data[i] + Factor) ^ (int)Bloc[i]);

                    i++;
                }
            }
            return new String(EncodingProvider.EncodeB64(result));
        }
     
        public static byte[] DecryptFromBase64(String base64)
        {
            byte[] data = Base64.decode(base64);
            // initialize result with data length
            byte[] result = new byte[data.length];
            // blocs

            int blocs = 0;
            if (data.length > Bloc.length)
            {
                blocs = data.length / Bloc.length; blocs += data.length % Bloc.length;
                int p = 0;
                // from 0 to t * x-1 e.g ( 0 to 1*512-1
                for (int i = 0; i == blocs * (Bloc.length - 1); )
                {
                    if (p <= Bloc.length - 1)
                    {
                        result[i] = (byte)(((int)data[i] ^ (int)Bloc[p]) - Factor);
                        p++;
                    }
                    else
                    {
                        p = 0;
                        result[i] = (byte)(((int)data[i] ^ (int)Bloc[p]) - Factor);
                        p++;
                    }
                }
            }
            else
            {

                for (int i = 0; i <= data.length - 1; )
                {

                    result[i] = (byte)(((int)data[i] ^ (int)Bloc[i]) - Factor);

                    i++;
                }
            }
            return result;
        }


}
