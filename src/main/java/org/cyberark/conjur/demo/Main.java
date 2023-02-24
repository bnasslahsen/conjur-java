package org.cyberark.conjur.demo;

import java.io.IOException;
import java.util.Map;

import com.cyberark.conjur.sdk.AccessToken;
import com.cyberark.conjur.sdk.ApiClient;
import com.cyberark.conjur.sdk.ApiException;
import com.cyberark.conjur.sdk.endpoint.SecretsApi;
import org.apache.commons.lang3.StringUtils;

/**
 * @author bnasslahsen
 */
public class Main {
	private static final String VARIABLE = ":variable:";

	public static void main(String[] args)  {

		ApiClient conjurClient = com.cyberark.conjur.sdk.Configuration.getDefaultApiClient();
		AccessToken accesToken = conjurClient.getNewAccessToken();
		if (accesToken == null) {
			System.err.println("Access token is null, Please enter proper environment variables.");
		}
		else {
			String token = accesToken.getHeaderValue();
			conjurClient.setAccessToken(token);
		}

		StringBuilder bulkSecrets = new StringBuilder();
		String urlConjurKey = conjurClient.getAccount() + VARIABLE + "data/vault/bnl-ocp-safe/Database-MySQL-jdbch2memtestdb-h2-user/address";
		bulkSecrets.append(urlConjurKey).append(",");
		String usernameConjurKey = conjurClient.getAccount() + VARIABLE + "data/vault/bnl-ocp-safe/Database-MySQL-jdbch2memtestdb-h2-user/username";
		bulkSecrets.append(usernameConjurKey).append(",");
		String passwordConjurKey = conjurClient.getAccount() + VARIABLE + "data/vault/bnl-ocp-safe/Database-MySQL-jdbch2memtestdb-h2-user/password";
		bulkSecrets.append(passwordConjurKey);

		try {
			SecretsApi secretsApi = new SecretsApi();
			Map<String, String> response = (Map<String, String>) secretsApi.getSecrets(bulkSecrets.toString());
			System.out.println("url=" + response.get(urlConjurKey));
			System.out.println("username=" + response.get(usernameConjurKey));
			System.out.println("password=" + response.get(passwordConjurKey));
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