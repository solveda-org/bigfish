package com.osafe.services.ebs;

/**
 * The Class RC4.
 */
class RC4 {
 
 /** The state. */
 private byte state[] = new byte[256];
 
 /** The x. */
 private int x;
 
 /** The y. */
 private int y;
 
 /**
  * Initializes the class with a string key. The length
  * of a normal key should be between 1 and 2048 bits.  But
  * this method doens't check the length at all.
  *
  * @param key   the encryption/decryption key
  * @throws NullPointerException the null pointer exception
  */
  RC4(String key) throws NullPointerException {
     this(key.getBytes());
 }

 /**
  * Initializes the class with a byte array key.  The length
  * of a normal key should be between 1 and 2048 bits.  But
  * this method doens't check the length at all.
  *
  * @param key   the encryption/decryption key
  * @throws NullPointerException the null pointer exception
  */
  RC4(byte[] key) throws NullPointerException {

     for (int i=0; i < 256; i++) {
         state[i] = (byte)i;
     }
     
     x = 0;
     y = 0;
     
     int index1 = 0;
     int index2 = 0;
     
     byte tmp;
     
     if (key == null || key.length == 0) {
         throw new NullPointerException();
     }
     
     for (int i=0; i < 256; i++) {

         index2 = ((key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;

         tmp = state[i];
         state[i] = state[index2];
         state[index2] = tmp;
         
         index1 = (index1 + 1) % key.length;
     }



 }

 /** 
  * RC4 encryption/decryption.
  *
  * @param data  the data to be encrypted/decrypted
  * @return the result of the encryption/decryption
  */
  byte[] rc4(String data) {
     
     if (data == null) {
         return null;
     }
     
     byte[] tmp = data.getBytes();
     
     this.rc4(tmp);
     
     return tmp;
 }

 /** 
  * RC4 encryption/decryption.
  *
  * @param buf  the data to be encrypted/decrypted
  * @return the result of the encryption/decryption
  */
  byte[] rc4(byte[] buf) {

     //int lx = this.x;
     //int ly = this.y;
     
     int xorIndex;
     byte tmp;
     
     if (buf == null) {
         return null;
     }
     
     byte[] result = new byte[buf.length];
     
     for (int i=0; i < buf.length; i++) {

         x = (x + 1) & 0xff;
         y = ((state[x] & 0xff) + y) & 0xff;

         tmp = state[x];
         state[x] = state[y];
         state[y] = tmp;
         
         xorIndex = ((state[x] &0xff) + (state[y] & 0xff)) & 0xff;
         result[i] = (byte)(buf[i] ^ state[xorIndex]);
     }
     
     //this.x = lx;
     //this.y = ly;
     
     return result;
 }
  
}