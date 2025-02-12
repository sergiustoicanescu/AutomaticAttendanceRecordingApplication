import Cookies from 'js-cookie';
import { SERVER_URL } from './constant';

export const fetchWithRedirect = async (path, options = {}) => {
    const { method = 'GET', body = null, headers = {} } = options;

    const fetchOptions = {
        method: method,
        credentials: 'include',
        redirect: 'follow',
        headers: {
            ...headers,
            ...(!headers['Content-Type'] && body ? { 'Content-Type': 'application/json' } : {}),
        },
        ...(body && (!headers['Content-Type'] || headers['Content-Type'] === 'application/json') ? { body: JSON.stringify(body) } : { body }),
    };
    const response = await fetch(`${SERVER_URL}${path}`, fetchOptions);

    if (response.redirected) {
        Cookies.set('preLoginPath', window.location.href, {
            expires: 1 / 24,
            secure: true,
            httpOnly: false,
            sameSite: 'lax',
            domain: process.env.REACT_APP_DOMAIN
        });
        window.location.href = response.url;
        return null;
    }

    return response;
};

export default fetchWithRedirect;
