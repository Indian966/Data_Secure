
import java.security.*;

import javax.crypto.*;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

public class RSATest {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {
		
		System.out.println("1. 키생성");
		KeyPair kp = generateRSAKeyPair(); 
		PublicKey publicKey = kp.getPublic();
		PrivateKey privateKey = kp.getPrivate();
		byte[] pubk = publicKey.getEncoded();
        byte[] prik = privateKey.getEncoded();
        System.out.println("Public Key : " + ByteUtils.toHexString(pubk));
        System.out.println("Private Key : " + ByteUtils.toHexString(prik));
        System.out.println();
        
        System.out.println("2. 암호화");
		String text = "아~ 인생은 나그네길~";
		System.out.println("평문 : "+text);
		byte[] t0 = text.getBytes();
		byte[] b0 = rsaEncrypt(t0, publicKey);
		byte[] t1 = rsaDecrypt(b0, privateKey);
		System.out.println("원래 평문 (바이트) : " + ByteUtils.toHexString(t0));
		System.out.println("암  호   문(바이트) : " + ByteUtils.toHexString(b0));
		System.out.println("복구 평문 (바이트) : " + ByteUtils.toHexString(t1));
		System.out.println("복호화 :"+new String(t1));
		System.out.println();
		
        System.out.println("3. 전자서명");
        byte[] s0 = rsaSign(t0, privateKey);
        boolean result = rsaVerify(t0, s0, publicKey);
        System.out.println("서명문 (바이트) : " + ByteUtils.toHexString(b0));
        System.out.println("서명 검증 (T/F) : " + result );

	}
	
	public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg  = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		return kpg.genKeyPair();
	}
	
	public static byte[] rsaEncrypt(byte[] plain, PublicKey pubk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, pubk);
        byte[] cipher = c.doFinal(plain);
        return cipher;
	}
		
	public static byte[] rsaDecrypt(byte[] cipher, PrivateKey privk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, privk);
        byte[] plain = c.doFinal(cipher);
        return plain;
	}
	
	public static byte[] rsaSign(byte[] plain, PrivateKey privk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {
		Signature sig = Signature.getInstance("MD5WithRSA");
		sig.initSign(privk);
		sig.update(plain);
		byte[] signatureBytes = sig.sign();
		return signatureBytes;
	}
	
	public static boolean rsaVerify(byte[] plain, byte[] sign, PublicKey pubk) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {
		Signature sig = Signature.getInstance("MD5WithRSA");
		sig.initVerify(pubk);
		sig.update(plain);
		return sig.verify(sign); 
	}
	
}