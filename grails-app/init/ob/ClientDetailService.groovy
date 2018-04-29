package ob

import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationException

/**
 * Created by songtao on 2018/3/27.
 */
class ClientDetailService implements ClientDetailsService{
    @Override
    ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return null
    }
}
