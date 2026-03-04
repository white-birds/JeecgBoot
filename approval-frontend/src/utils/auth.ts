import Keycloak from 'keycloak-js';

// 这里是 Keycloak 的连接配置
const keycloak = new Keycloak({
    url: 'http://localhost:8080',
    realm: 'master',
    clientId: 'aihom-system'
});

export const initKeycloak = (onAuthenticated: () => void) => {
    keycloak.init({
        onLoad: 'login-required', // 没登录直接跳 Keycloak，不给看你的登录页
        checkLoginIframe: false,
        pkceMethod: 'S256'
    }).then((authenticated) => {
        if (authenticated) {
            // 登录成功，把 Token 存入 localStorage，你的 http.ts 拦截器会自动读取它
            localStorage.setItem('token', keycloak.token || '');
            localStorage.setItem('userInfo', JSON.stringify(keycloak.tokenParsed));

            // 每分钟检查一次 Token 是否过期，如果快过期了就静默刷新
            setInterval(() => {
                keycloak.updateToken(70).then((refreshed) => {
                    if (refreshed) {
                        localStorage.setItem('token', keycloak.token || '');
                        console.log('Token 已刷新');
                    }
                });
            }, 60000);

            onAuthenticated();
        }
    }).catch((err) => {
        console.error("Keycloak 认证失败", err);
    });
};

export default keycloak;