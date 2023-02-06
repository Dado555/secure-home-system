package rs.securehome.admin.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import rs.securehome.admin.util.jwt.JwtFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtTokenFilter;
    private final PasswordEncoder passwordEncoder;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtTokenFilter, PasswordEncoder passwordEncoder, RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenFilter = jwtTokenFilter;
        this.passwordEncoder = passwordEncoder;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(this.passwordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.GET, "/", "/js/**", "/css/**", "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String readCertificates = "READ_CERTIFICATES";
        final String readCertificate = "READ_CERTIFICATE";
        final String revokeCertificate = "REVOKE_CERTIFICATE";
        final String approveRequest = "APPROVE_REQUEST";
        final String readRequests = "READ_REQUESTS";
        final String editRequest = "EDIT_REQUEST";
        final String logout = "LOGOUT";
        final String readUsers = "READ_USERS";
        final String upgradeTenantRole = "UPGRADE_TENANT_ROLE";
        final String readHouseholds = "READ_HOUSEHOLDS";
        final String readRealEstatesForHousehold = "READ_REAL_ESTATES_FOR_HOUSEHOLD";
        final String readVisibleRealEstate = "READ_VISIBLE_REAL_ESTATE";
        final String deleteUser = "DELETE_USER";
        final String deleteRealEstate = "DELETE_REAL_ESTATE";
        final String createTenant = "CREATE_TENANT";
        final String updateTenant = "UPDATE_TENANT";
        final String updateTenantRole = "UPDATE_TENANT_ROLE";
        final String changeRealEstateVisibility = "CHANGE_REAL_ESTATE_VISIBILITY";
        final String readUsersForHousehold = "READ_USERS_FOR_HOUSEHOLD";
        final String readHouseholdsForUser = "READ_HOUSEHOLDS_FOR_USER";
        final String updateRealEstate = "UPDATE_REAL_ESTATE";
        final String createRealEstate = "CREATE_REAL_ESTATE";
        final String changeDefaultPassword = "CHANGE_DEFAULT_PASSWORD";
        final String readDevices = "READ_DEVICES";
        final String updateDevices = "UPDATE_DEVICES";
        final String createDevices = "CREATE_DEVICES";
        final String deleteDevices = "DELETE_DEVICES";
        final String readSingleRealEstate = "READ_SINGLE_REAL_ESTATE";
        final String readLogs = "READ_LOGS";
        final String checkDeviceOwnership = "CHECK_DEVICE_OWNERSHIP";
        final String viewAlarmsAdmin = "VIEW_ALARMS_ADMIN";
        final String createRule = "CREATE_RULE";
        final String readRules = "READ_RULES";
        final String checkPrivilege = "CHECK_PRIVILEGE";
        final String readDevicesForHousehold = "READ_DEVICES_FOR_HOUSEHOLD";
        final String createNewCustomLogRule = "CREATE_NEW_CUSTOM_LOG_RULE";
        final String deleteCustomLogRule = "DELETE_CUSTOM_LOG_RULE";
        final String readCustomLogRules = "READ_CUSTOM_LOG_RULES";

        //TODO: ovu liniju ispod provjeriti
        http = http.cors().and().csrf().disable();

        http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);

        http.authorizeRequests()
                //staticki sadrzaj
//                .antMatchers(HttpMethod.GET, "/").permitAll()
//                .antMatchers(HttpMethod.GET, "/js/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/css/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/favicon.ico").permitAll()

                //api
                .antMatchers(HttpMethod.POST, "/api/users/authenticate").permitAll()
                .antMatchers(HttpMethod.POST, "/api/requests").permitAll()
                .antMatchers(HttpMethod.GET, "/api/devices/{id}").permitAll() // OVO SAMO PRIVREMENO, HOUSE APLIKACIJA PREUZIMA KONFIGURACIJU UREDJAJA

                //ovo 2 najbolje staviti da moze samo klijentska aplikacija da provjerava, mozda pomocu nekog api kljuca
                .antMatchers(HttpMethod.GET, "/api/certificates/valid/{alias}").permitAll()
                .antMatchers(HttpMethod.POST, "/api/certificates/valid-cer").permitAll()

                //ovo je otvoreno svima
                .antMatchers(HttpMethod.GET, "/api/certificates/download-certificate").permitAll()
                .antMatchers(HttpMethod.POST, "/api/certificates/download-key").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rules/alarm-should-be-fired").permitAll()
                // ovo ispod mogu svi korisnici
                .antMatchers(HttpMethod.GET, "/api/users/logout").hasAuthority(logout)
                .antMatchers(HttpMethod.PUT, "/api/users/{id}/change-default-password").hasAuthority(changeDefaultPassword)
                .antMatchers(HttpMethod.POST, "/api/privileges/check").hasAuthority(checkPrivilege)
                .antMatchers(HttpMethod.GET, "/api/devices/household").hasAuthority(readDevicesForHousehold)

                //ove ispod moze samo admin
                .antMatchers(HttpMethod.POST, "/api/requests/approve/{id}").hasAuthority(approveRequest)
                .antMatchers(HttpMethod.GET, "/api/certificates").hasAuthority(readCertificates)
                .antMatchers(HttpMethod.GET, "/api/certificates/{alias}").hasAuthority(readCertificate)
                .antMatchers(HttpMethod.POST, "/api/certificates/revoke").hasAuthority(revokeCertificate)
                .antMatchers(HttpMethod.GET, "/api/requests/getAll").hasAuthority(readRequests)
                .antMatchers(HttpMethod.PUT, "/api/requests/{id}").hasAuthority(editRequest)

                .antMatchers(HttpMethod.GET, "/api/users").hasAuthority(readUsers)
                .antMatchers(HttpMethod.PUT, "/api/users/tenant/role").hasAuthority(updateTenantRole)
                .antMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAuthority(deleteUser)

                .antMatchers(HttpMethod.GET, "/api/households").hasAuthority(readHouseholds)

                .antMatchers(HttpMethod.GET, "/api/real-estate/household/{id}").hasAuthority(readRealEstatesForHousehold)
                .antMatchers(HttpMethod.DELETE, "/api/real-estate/{id}").hasAuthority(deleteRealEstate)

                .antMatchers(HttpMethod.POST, "/api/devices").hasAuthority(createDevices)
                .antMatchers(HttpMethod.PUT, "/api/devices/{id}").hasAuthority(updateDevices)
                .antMatchers(HttpMethod.DELETE, "/api/devices/{id}").hasAuthority(deleteDevices)

                .antMatchers(HttpMethod.GET, "/api/logs").hasAuthority(readLogs)

                .antMatchers(HttpMethod.POST, "/api/rules/temperature-rules").hasAuthority(createRule)
                .antMatchers(HttpMethod.POST, "/api/rules/frequency-rules").hasAuthority(createRule)

                .antMatchers(HttpMethod.POST, "/api/alarms/log-alarm").hasAuthority(createNewCustomLogRule)
                .antMatchers(HttpMethod.GET, "/api/alarms/log-alarm").hasAuthority(readCustomLogRules)
                .antMatchers(HttpMethod.DELETE, "/api/alarms/log-alarm/{id}").hasAuthority(deleteCustomLogRule)


                .antMatchers(HttpMethod.GET, "/api/rules/temperature-rules").hasAuthority(readRules)
                .antMatchers(HttpMethod.GET, "/api/rules/frequency-rules").hasAuthority(readRules)


                //ove ispod moze samo glava kuce
                .antMatchers(HttpMethod.POST, "/api/users/tenant").hasAuthority(createTenant)
                .antMatchers(HttpMethod.PUT, "/api/users/tenant/{id}").hasAuthority(updateTenant)
                .antMatchers(HttpMethod.PUT, "/api/users/tenant/role/{id}").hasAuthority(upgradeTenantRole)
                .antMatchers(HttpMethod.PUT, "/api/real-estate/visibility/{type}").hasAuthority(changeRealEstateVisibility)
                .antMatchers(HttpMethod.POST, "/api/real-estate/create").hasAuthority(createRealEstate)
                .antMatchers(HttpMethod.PUT, "/api/real-estate/update").hasAuthority(updateRealEstate)
                .antMatchers(HttpMethod.PUT, "/api/real-estate/update-name").hasAuthority(updateRealEstate)

                // ove ispod mogu svi
                .antMatchers(HttpMethod.GET, "/api/users/household/{id}").hasAuthority(readUsersForHousehold)
                .antMatchers(HttpMethod.GET, "/api/users/household-id/{id}").hasAuthority(readUsersForHousehold)
                .antMatchers(HttpMethod.GET, "/api/devices/real-estate/{id}").hasAuthority(readDevices)
                .antMatchers(HttpMethod.GET, "/api/real-estate/{id}").hasAuthority(readSingleRealEstate)

                // ove ispod mogu stanari kuce
                .antMatchers(HttpMethod.GET, "/api/households/user/{id}").hasAuthority(readHouseholdsForUser)
                .antMatchers(HttpMethod.GET, "/api/real-estate/tenant/{id}").hasAuthority(readVisibleRealEstate)
                .antMatchers(HttpMethod.GET, "/api/devices/ownership").hasAuthority(checkDeviceOwnership)

                .antMatchers(HttpMethod.GET, "/api/rules").permitAll()
                .antMatchers(HttpMethod.GET, "/api/alarms").hasAuthority(viewAlarmsAdmin)
                .anyRequest().authenticated();

        //TODO: ovo vidi
        http.headers().xssProtection().and().contentSecurityPolicy("script-src 'self'");

        http.addFilterBefore(jwtTokenFilter,
                BasicAuthenticationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}
