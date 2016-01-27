package ppke.kripto;

import java.math.BigInteger;
import java.util.HashMap;

public class DH {

	private long gen;
	private BigInteger mod;
	private long priv;
	private long result;
	private long maxNum = (((long) 1) << 31) - 1;

	
	private static final int DH_RANGE = 100;

	public DH() {
	}

	public DH(long generator, long modulus) throws Exception {
		if (generator >= maxNum || modulus >= maxNum)
			throw new Exception("Modulus or generator too large.");
//		new BigInteger(MOD_STRINGS[1],16);
		gen = generator;
		for(int i=0; i<MOD_NUMS.length; i++){
			modHash.put(MOD_NUMS[i], MOD_STRINGS[i]);
		}
		mod = IETF.get(modulus);
		priv = generatePrime();
		System.out.println(priv);
	}

	private long rng(long limit) {
		return (long) (java.lang.Math.random() * limit);
	}

	// Performs the miller-rabin primality test on a guessed prime n.
	// trials is the number of attempts to verify this, because the function
	// is not 100% accurate it may be a composite. However setting the trial
	// value to around 5 should guarantee success even with very large primes
	private boolean millerRabin(long n, int trials) {
		long a = 0;
		for (int i = 0; i < trials; i++) {
			a = rng(n - 3) + 2;// gets random value in [2..n-1]
			if (XpowYmodN(a, n - 1, n) != 1)
				return false; // n composite, return false
		}
		return true; // n probably prime
	}

	// Generates a large prime number by
	// choosing a randomly large integer, and ensuring the value is odd
	// then uses the miller-rabin primality test on it to see if it is prime
	// if not the value gets increased until it is prime
	private long generatePrime() {
		long prime = 0;
		do {
			long start = rng(maxNum);
			prime = tryToGeneratePrime(start);
		} while (prime == 0);
		return prime;
	}

	private long tryToGeneratePrime(long prime) {
		// ensure it is an odd number
		if ((prime & 1) == 0)
			prime += 1;
		long c = 0;
		while (!millerRabin(prime, 25) && (c++ < DH_RANGE) && prime < maxNum) {
			prime += 2;
			if ((prime % 3) == 0)
				prime += 2;
		}
		return (c >= DH_RANGE || prime >= maxNum) ? 0 : prime;
	}

	// Raises X to the power Y in modulus N
	// the values of X, Y, and N can be massive, and this can be
	// achieved by first calculating X to the power of 2 then
	// using power chaining over modulus N
	private long XpowYmodN(long x, long y, long N) {
		long result = 1;
		final long oneShift63 = ((long) 1) << 63;
		for (int i = 0; i < 64; y <<= 1, i++) {
			result = result * result % N;
			if ((y & oneShift63) != 0)
				result = result * x % N;
		}
		return result;
	}

	// public void createKeys() {
	// gen = generatePrime();
	// mod = generatePrime();
	// if (gen > mod) {
	// long swap = gen;
	// gen = mod;
	// mod = swap;
	// }
	// }

	public long getMyresult() {
		// priv = rng(maxNum);
		return result = XpowYmodN(gen, priv, mod.longValue());
	}

	public long createEncryptionKey(long result) throws Exception {
		if (result >= maxNum) {
			throw new Exception("interKey too large");
		}

		System.out.println(XpowYmodN(result, priv, mod.longValue()));
		return XpowYmodN(result, priv, mod.longValue());

	}

	public BigInteger createKey(long result){
		BigInteger key = new BigInteger(Long.toString(result));
		return key.modPow(new BigInteger(Long.toString(priv)),mod);
	}
	
//	public BigInteger modPow(BigInteger exponent, BigInteger m) {
//		if (m.isNegative() || m.isZero())
//			throw new ArithmeticException("non-positive modulo");
//		if (exponent.isNegative())
//			return modInverse(m).modPow(exponent.negate(), m);
//		if (exponent.isOne())
//			return mod(m);
//		BigInteger s = ONE;
//		BigInteger t = this;
//		BigInteger u = exponent;
//		while (!u.isZero()) {
//			if (u.and(ONE).isOne())
//				s = times(s, t).mod(m);
//			u = u.shiftRight(1);
//			t = times(t, t).mod(m);
//		}
//		return s;
//	}

	public int bits(long number) {
		for (int i = 0; i < 64; i++) {
			number /= 2;
			if (number < 2)
				return i;
		}
		return 0;
	}

	public static byte[] longToBytes(long number) {
		byte[] bytes = new byte[8];
		for (int i = 0; i < 8; i++) {
			bytes[i] = (byte) (0xff & (number >> (8 * (7 - i))));
		}
		return bytes;
	}

	public static long bytesToLong(byte[] bytes) {
		long result = 0;
		for (int i = 0; i < 8; i++) {
			result <<= 8;
			result += (byte) bytes[i];
		}
		return result;
	}

	public static HashMap<Integer,String> modHash = new HashMap<Integer, String>();
	
	public static Integer[] MOD_NUMS = {  768, 1024, 1536, 2048, 3072, 4096,
			6144, 8192 };

	public static String[] MOD_STRINGS = {
			
			"FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 "
					+ "29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD "
					+ "EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245 "
					+ "E485B576 625E7EC6 F44C42E9 A63A3620 FFFFFFFF FFFFFFFF",
			"FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 "
					+ "29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD "
					+ "EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245 "
					+ "E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED "
					+ "EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE65381 "
					+ "FFFFFFFF FFFFFFFF",
			"FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 "
					+ "29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD "
					+ "EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245 "
					+ "E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED "
					+ "EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D "
					+ "C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F "
					+ "83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D "
					+ "670C354E 4ABC9804 F1746C08 CA237327 FFFFFFFF FFFFFFFF",
			"FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 "
					+ "29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD "
					+ "EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245 "
					+ "E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED "
					+ "EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D "
					+ "C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F "
					+ "83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D "
					+ "670C354E 4ABC9804 F1746C08 CA18217C 32905E46 2E36CE3B "
					+ "E39E772C 180E8603 9B2783A2 EC07A28F B5C55DF0 6F4C52C9 "
					+ "DE2BCBF6 95581718 3995497C EA956AE5 15D22618 98FA0510 "
					+ "15728E5A 8AACAA68 FFFFFFFF FFFFFFFF",
			"FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 "
					+ "29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD "
					+ "EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245 "
					+ "E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED "
					+ "EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D "
					+ "C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F "
					+ "83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D "
					+ "670C354E 4ABC9804 F1746C08 CA18217C 32905E46 2E36CE3B "
					+ "E39E772C 180E8603 9B2783A2 EC07A28F B5C55DF0 6F4C52C9 "
					+ "DE2BCBF6 95581718 3995497C EA956AE5 15D22618 98FA0510 "
					+ "15728E5A 8AAAC42D AD33170D 04507A33 A85521AB DF1CBA64 "
					+ "ECFB8504 58DBEF0A 8AEA7157 5D060C7D B3970F85 A6E1E4C7 "
					+ "ABF5AE8C DB0933D7 1E8C94E0 4A25619D CEE3D226 1AD2EE6B "
					+ "F12FFA06 D98A0864 D8760273 3EC86A64 521F2B18 177B200C "
					+ "BBE11757 7A615D6C 770988C0 BAD946E2 08E24FA0 74E5AB31 "
					+ "43DB5BFC E0FD108E 4B82D120 A93AD2CA FFFFFFFF FFFFFFFF ",
			"FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 "
					+ "29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD "
					+ "EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245 "
					+ "E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED "
					+ "EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D "
					+ "C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F "
					+ "83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D "
					+ "670C354E 4ABC9804 F1746C08 CA18217C 32905E46 2E36CE3B "
					+ "E39E772C 180E8603 9B2783A2 EC07A28F B5C55DF0 6F4C52C9 "
					+ "DE2BCBF6 95581718 3995497C EA956AE5 15D22618 98FA0510 "
					+ "15728E5A 8AAAC42D AD33170D 04507A33 A85521AB DF1CBA64 "
					+ "ECFB8504 58DBEF0A 8AEA7157 5D060C7D B3970F85 A6E1E4C7 "
					+ "ABF5AE8C DB0933D7 1E8C94E0 4A25619D CEE3D226 1AD2EE6B "
					+ "F12FFA06 D98A0864 D8760273 3EC86A64 521F2B18 177B200C "
					+ "BBE11757 7A615D6C 770988C0 BAD946E2 08E24FA0 74E5AB31 "
					+ "43DB5BFC E0FD108E 4B82D120 A9210801 1A723C12 A787E6D7 "
					+ "88719A10 BDBA5B26 99C32718 6AF4E23C 1A946834 B6150BDA "
					+ "2583E9CA 2AD44CE8 DBBBC2DB 04DE8EF9 2E8EFC14 1FBECAA6 "
					+ "287C5947 4E6BC05D 99B2964F A090C3A2 233BA186 515BE7ED "
					+ "1F612970 CEE2D7AF B81BDD76 2170481C D0069127 D5B05AA9 "
					+ "93B4EA98 8D8FDDC1 86FFB7DC 90A6C08F 4DF435C9 34063199 "
					+ "FFFFFFFF FFFFFFFF",
			"FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 "
					+ "29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD "
					+ "EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245 "
					+ "E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED "
					+ "EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D "
					+ "C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F "
					+ "83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D "
					+ "670C354E 4ABC9804 F1746C08 CA18217C 32905E46 2E36CE3B "
					+ "E39E772C 180E8603 9B2783A2 EC07A28F B5C55DF0 6F4C52C9 "
					+ "DE2BCBF6 95581718 3995497C EA956AE5 15D22618 98FA0510 "
					+ "15728E5A 8AAAC42D AD33170D 04507A33 A85521AB DF1CBA64 "
					+ "ECFB8504 58DBEF0A 8AEA7157 5D060C7D B3970F85 A6E1E4C7 "
					+ "ABF5AE8C DB0933D7 1E8C94E0 4A25619D CEE3D226 1AD2EE6B "
					+ "F12FFA06 D98A0864 D8760273 3EC86A64 521F2B18 177B200C "
					+ "BBE11757 7A615D6C 770988C0 BAD946E2 08E24FA0 74E5AB31 "
					+ "43DB5BFC E0FD108E 4B82D120 A9210801 1A723C12 A787E6D7 "
					+ "88719A10 BDBA5B26 99C32718 6AF4E23C 1A946834 B6150BDA "
					+ "2583E9CA 2AD44CE8 DBBBC2DB 04DE8EF9 2E8EFC14 1FBECAA6 "
					+ "287C5947 4E6BC05D 99B2964F A090C3A2 233BA186 515BE7ED "
					+ "1F612970 CEE2D7AF B81BDD76 2170481C D0069127 D5B05AA9 "
					+ "93B4EA98 8D8FDDC1 86FFB7DC 90A6C08F 4DF435C9 34028492 "
					+ "36C3FAB4 D27C7026 C1D4DCB2 602646DE C9751E76 3DBA37BD "
					+ "F8FF9406 AD9E530E E5DB382F 413001AE B06A53ED 9027D831 "
					+ "179727B0 865A8918 DA3EDBEB CF9B14ED 44CE6CBA CED4BB1B "
					+ "DB7F1447 E6CC254B 33205151 2BD7AF42 6FB8F401 378CD2BF "
					+ "5983CA01 C64B92EC F032EA15 D1721D03 F482D7CE 6E74FEF6 "
					+ "D55E702F 46980C82 B5A84031 900B1C9E 59E7C97F BEC7E8F3 "
					+ "23A97A7E 36CC88BE 0F1D45B7 FF585AC5 4BD407B2 2B4154AA "
					+ "CC8F6D7E BF48E1D8 14CC5ED2 0F8037E0 A79715EE F29BE328 "
					+ "06A1D58B B7C5DA76 F550AA3D 8A1FBFF0 EB19CCB1 A313D55C "
					+ "DA56C9EC 2EF29632 387FE8D7 6E3C0468 043E8F66 3F4860EE "
					+ "12BF2D5B 0B7474D6 E694F91E 6DCC4024 FFFFFFFF FFFFFFFF",
			"FFFFFFFF FFFFFFFF C90FDAA2 2168C234 C4C6628B 80DC1CD1 "
					+ "29024E08 8A67CC74 020BBEA6 3B139B22 514A0879 8E3404DD "
					+ "EF9519B3 CD3A431B 302B0A6D F25F1437 4FE1356D 6D51C245 "
					+ "E485B576 625E7EC6 F44C42E9 A637ED6B 0BFF5CB6 F406B7ED "
					+ "EE386BFB 5A899FA5 AE9F2411 7C4B1FE6 49286651 ECE45B3D "
					+ "C2007CB8 A163BF05 98DA4836 1C55D39A 69163FA8 FD24CF5F "
					+ "83655D23 DCA3AD96 1C62F356 208552BB 9ED52907 7096966D "
					+ "670C354E 4ABC9804 F1746C08 CA18217C 32905E46 2E36CE3B "
					+ "E39E772C 180E8603 9B2783A2 EC07A28F B5C55DF0 6F4C52C9 "
					+ "DE2BCBF6 95581718 3995497C EA956AE5 15D22618 98FA0510 "
					+ "15728E5A 8AAAC42D AD33170D 04507A33 A85521AB DF1CBA64 "
					+ "ECFB8504 58DBEF0A 8AEA7157 5D060C7D B3970F85 A6E1E4C7 "
					+ "ABF5AE8C DB0933D7 1E8C94E0 4A25619D CEE3D226 1AD2EE6B "
					+ "F12FFA06 D98A0864 D8760273 3EC86A64 521F2B18 177B200C "
					+ "BBE11757 7A615D6C 770988C0 BAD946E2 08E24FA0 74E5AB31 "
					+ "43DB5BFC E0FD108E 4B82D120 A9210801 1A723C12 A787E6D7 "
					+ "88719A10 BDBA5B26 99C32718 6AF4E23C 1A946834 B6150BDA "
					+ "2583E9CA 2AD44CE8 DBBBC2DB 04DE8EF9 2E8EFC14 1FBECAA6 "
					+ "287C5947 4E6BC05D 99B2964F A090C3A2 233BA186 515BE7ED "
					+ "1F612970 CEE2D7AF B81BDD76 2170481C D0069127 D5B05AA9 "
					+ "93B4EA98 8D8FDDC1 86FFB7DC 90A6C08F 4DF435C9 34028492 "
					+ "36C3FAB4 D27C7026 C1D4DCB2 602646DE C9751E76 3DBA37BD "
					+ "F8FF9406 AD9E530E E5DB382F 413001AE B06A53ED 9027D831 "
					+ "179727B0 865A8918 DA3EDBEB CF9B14ED 44CE6CBA CED4BB1B "
					+ "DB7F1447 E6CC254B 33205151 2BD7AF42 6FB8F401 378CD2BF "
					+ "5983CA01 C64B92EC F032EA15 D1721D03 F482D7CE 6E74FEF6 "
					+ "D55E702F 46980C82 B5A84031 900B1C9E 59E7C97F BEC7E8F3 "
					+ "23A97A7E 36CC88BE 0F1D45B7 FF585AC5 4BD407B2 2B4154AA "
					+ "CC8F6D7E BF48E1D8 14CC5ED2 0F8037E0 A79715EE F29BE328 "
					+ "06A1D58B B7C5DA76 F550AA3D 8A1FBFF0 EB19CCB1 A313D55C "
					+ "DA56C9EC 2EF29632 387FE8D7 6E3C0468 043E8F66 3F4860EE "
					+ "12BF2D5B 0B7474D6 E694F91E 6DBE1159 74A3926F 12FEE5E4 "
					+ "38777CB6 A932DF8C D8BEC4D0 73B931BA 3BC832B6 8D9DD300 "
					+ "741FA7BF 8AFC47ED 2576F693 6BA42466 3AAB639C 5AE4F568 "
					+ "3423B474 2BF1C978 238F16CB E39D652D E3FDB8BE FC848AD9 "
					+ "22222E04 A4037C07 13EB57A8 1A23F0C7 3473FC64 6CEA306B "
					+ "4BCBC886 2F8385DD FA9D4B7F A2C087E8 79683303 ED5BDD3A "
					+ "062B3CF5 B3A278A6 6D2A13F8 3F44F82D DF310EE0 74AB6A36 "
					+ "4597E899 A0255DC1 64F31CC5 0846851D F9AB4819 5DED7EA1 "
					+ "B1D510BD 7EE74D73 FAF36BC3 1ECFA268 359046F4 EB879F92 "
					+ "4009438B 481C6CD7 889A002E D5EE382B C9190DA6 FC026E47 "
					+ "9558E447 5677E9AA 9E3050E2 765694DF C81F56E8 80B96E71 "
					+ "60C980DD 98EDD3DF FFFFFFFF FFFFFFFF" };
}