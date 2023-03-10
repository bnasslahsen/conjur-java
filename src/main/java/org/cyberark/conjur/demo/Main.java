package org.cyberark.conjur.demo;

import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
import org.apache.commons.lang3.StringUtils;

/**
 * @author bnasslahsen
 */
public class Main {
	private static final String VARIABLE = "variable";
	private static final String SECRET_USERNAME="data/vault/bnl-ocp-safe/Database-MySQL-OCPCCSITE1/username";
	private static final String SECRET_PASSWORD="data/vault/bnl-ocp-safe/Database-MySQL-OCPCCSITE1/password";

	public static void main(String[] args)  {

		// Initialize ApiClient
		ApiClient conjurClient = com.cyberark.conjur.sdk.Configuration.getDefaultApiClient();
		// Get Access Token
		AccessToken accessToken = conjurClient.getNewAccessToken();
		if (accessToken == null) {
			System.err.println("Access token is null, Please enter proper environment variables.");
		}
		else {
			String token = accessToken.getHeaderValue();
			conjurClient.setAccessToken(token);
		}

		try {
			SecretsApi secretsApi = new SecretsApi();
			String username = secretsApi.getSecret(conjurClient.getAccount(), VARIABLE, SECRET_USERNAME);
			System.out.println("username=" + username);
			String password = secretsApi.getSecret(conjurClient.getAccount(), VARIABLE, SECRET_PASSWORD);
			System.out.println("password=" + password);
		}
		catch (ApiException e) {
			String message = StringUtils.isEmpty(e.getMessage()) ? e.getResponseBody() : e.getMessage();
			throw new RuntimeException(message, e);
		}
		finally {
			conjurClient.getHttpClient().connectionPool().evictAll();
		}
	}

}