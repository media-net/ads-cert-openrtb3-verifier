package net.media.adscert.verification.service;

import net.media.adscert.exceptions.InvalidDataException;
import net.media.adscert.exceptions.ProcessException;
import net.media.adscert.models.OpenRTB;
import net.media.adscert.models.Source;
import net.media.adscert.utils.SignatureUtil;
import net.media.adscert.utils.DigestUtil;

import java.security.PublicKey;
import java.util.Map;

/**
 * A {@link VerificationService} provides means to verify digital signature. Following are <b>some</b> of the ways to verify:
 * <ol>
 *   <li>By passing {@link OpenRTB} request object</li>
 *   <li>By passing Public Key URL, Fields used to generate Digital Signature, Digital Signature and Map of fields used to sign the request</li>
 *   <li>By passing {@link PublicKey}, Fields used to generate Digital Signature, Digital Signature and Map of fields used to sign the request</li>
 *   <li>By passing Public Key URL, Fields used to generate Digital Signature, Digital Signature, Digest and Map of fields used to sign the request</li>
 *   <li>By passing {@link PublicKey}, Fields used to generate Digital Signature, Digital Signature, Digest and Map of fields used to sign the request</li>
 * </ol>
 *
 * In addition, a debug flag can be used to decide whether the provided digest should be used or not.
 *
 * @author pranav.a
 * @author anupam.v
 *
 * @since 1.0
 *
 */
public class VerificationService {

	/**
	 * Verifies an {@link OpenRTB} request.
	 *
	 * @param openRTB {@link OpenRTB} request
	 *
	 * @return  a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, false);
	}

	/**
	 *	Verifies the digital signature using public key url and digest fields.
	 *
	 * @param publicKeyURL url of the public key of the signing authority
	 * @param dsMap fields that were used for signing the request
	 * @param ds  digital signature in the request
	 * @param digestFields map of fields that were used for generating the signature and their values
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(String publicKeyURL,
	                             String dsMap,
	                             String ds,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		return verifyRequest(publicKeyURL, dsMap, ds, null, digestFields);
	}

	/**
	 * Verifies the digital signature using {@link PublicKey} and digest fields.
	 *
	 * @param publicKey {@link PublicKey} of the signing authority
	 * @param dsMap the fields that were used for signing the request
	 * @param ds  digital signature in the request
	 * @param digestFields  map of fields that were used for generating the signature and their values
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(PublicKey publicKey,
	                             String dsMap,
	                             String ds,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		return verifyRequest(publicKey, dsMap, ds, null, digestFields);
	}

	/**
	 * Verifies the digital signature using public key url and digest fields.
	 *
	 * @param publicKeyURL url of the public key of the signing authority
	 * @param dsMap the fields that were used for signing the request
	 * @param ds  digital signature in the request
	 * @param digest
	 * @param digestFields  map of fields that were used for generating the signature and their values
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(String publicKeyURL,
	                             String dsMap,
	                             String ds,
	                             String digest,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (publicKeyURL == null || publicKeyURL.length() == 0) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		try {
			PublicKey publicKey = SignatureUtil.getPublicKeyFromUrl(publicKeyURL);
			return verifyRequest(publicKey, dsMap, ds, digest, digestFields);
		} catch (Exception e) {
			throw new ProcessException(e);
		}
	}

	/**
	 *
	 * @param publicKey {@link PublicKey} of the signing authority
	 * @param dsMap the fields that were used for signing the request
	 * @param ds  digital signature in the request
	 * @param digest
	 * @param digestFields  map of fields that were used for generating the signature and their values
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(PublicKey publicKey,
	                             String dsMap,
	                             String ds,
	                             String digest,
	                             Map<String, String> digestFields) throws InvalidDataException, ProcessException {
		if (digest == null && digestFields == null) {
			throw new InvalidDataException("Digest Field Map is null");
		}
		if (publicKey == null) {
			throw new InvalidDataException("Public Key cannot be null");
		}
		if (ds == null || ds.length() == 0) {
			throw new InvalidDataException("Digital signature is empty");
		}
		if (dsMap == null || dsMap.length() == 0) {
			throw new InvalidDataException("DsMap is empty");
		}

		try {
			digest = digest == null
					? DigestUtil.getDigestFromDsMap(dsMap, digestFields)
					: digest;

			return SignatureUtil.verifySign(publicKey, digest, ds);
		} catch (Exception e) {
			throw new ProcessException("Error in verification", e);
		}
	}

	/**
	 *
	 * @param openRTB {@link OpenRTB} request
	 * @param debug
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
	                             Boolean debug) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, debug, null);
	}

	/**
	 *
	 * @param openRTB {@link OpenRTB} request
	 * @param publicKey {@link PublicKey} of the signing authority
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
	                             PublicKey publicKey) throws InvalidDataException, ProcessException {
		return verifyRequest(openRTB, false, publicKey);
	}

	/**
	 *
	 * @param openRTB {@link OpenRTB} request
	 * @param debug
	 * @param publicKey {@link PublicKey} of the signing authority
	 *
	 * @return a boolean stating whether the verification of the signature succeeded or not
	 *
	 * @throws InvalidDataException if the parameters are null or empty
	 * @throws ProcessException if an exception is thrown during the verification process
	 */
	public Boolean verifyRequest(OpenRTB openRTB,
	                             Boolean debug,
	                             PublicKey publicKey) throws InvalidDataException, ProcessException {
		if (openRTB == null) {
			throw new InvalidDataException("OpenRTB object is null");
		}

		Source source = openRTB.getRequest().getSource();

		if (publicKey == null && (source.getCert() == null || source.getCert().length() == 0)) {
			throw new InvalidDataException("Filename of certificate is empty");
		}
		if (source.getDs() == null || source.getDs().length() == 0) {
			throw new InvalidDataException("Digital signature is empty");
		}
		if (source.getDsmap() == null || source.getDsmap().length() == 0) {
			throw new InvalidDataException("DsMap is empty");
		}

		try {
			String digest = debug
					? DigestUtil.getDigest(openRTB)
					: DigestUtil.getDigestFromDsMap(openRTB);


			publicKey = publicKey == null
					? SignatureUtil.getPublicKeyFromUrl(source.getCert())
					: publicKey;

			return SignatureUtil.verifySign(publicKey, digest, source.getDs());
		} catch (Exception e) {
			throw new ProcessException("Error in verification", e);
		}
	}

}
