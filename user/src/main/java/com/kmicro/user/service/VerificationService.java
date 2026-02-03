package com.kmicro.user.service;

import com.google.common.hash.Hashing;
import com.kmicro.user.constants.AppContants;
import com.kmicro.user.entities.TokenEntity;
import com.kmicro.user.exception.NotExistException;
import com.kmicro.user.exception.RateLimitException;
import com.kmicro.user.repository.TokenRepository;
import com.kmicro.user.utils.RedisOps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;


@Slf4j
@Service
public class VerificationService {
    private final TokenRepository tokenRepository;
    private final RedisOps redisOps;
//    private final UserService userService;

    VerificationService(TokenRepository tokenRepository, RedisOps redisOps){
        this.tokenRepository = tokenRepository;
        this.redisOps = redisOps;
    }

    public TokenEntity verifyToken(String token) {
//        String hashedToken = hashedToken(
//                HtmlUtils.htmlEscape(
//                        UriUtils.encodeQueryParam(token.trim(), StandardCharsets.UTF_8)
//                )
//        );
        TokenEntity cachedToken = redisOps.getVerificationToken(token);

        TokenEntity verificationToken = null != cachedToken
                ? cachedToken
                : tokenRepository.findByToken(token).orElseThrow(()-> new NotExistException("Token not exists"));

        if(verificationToken.getIsVerified()) throw new RateLimitException("Token Already verified");
        // check Time, if 20 min window -> set token expire
        if(isTokenExpired(verificationToken)) throw new NotExistException("Token Expired.");
        return verificationToken;
    }

    public void updateToken(TokenEntity tokenEntity){
        tokenRepository.save(tokenEntity);
        redisOps.updateToken(tokenEntity);
    }

    public String generateToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String generateToken(Long userID) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public boolean isTokenExpired(TokenEntity token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public String hashedToken(String token) {
        return Hashing.sha256()
                .hashString(token, StandardCharsets.UTF_8)
                .toString();
    }

    public String generateNewToken(Long userID){
        String simpleToken = generateToken();
        TokenEntity token = new TokenEntity();
//        token.setToken(hashedToken(simpleToken));
        token.setToken(simpleToken);
        token.setUserId(userID);
        token.setIsVerified(false);
        token.setCreatedAt(Instant.now());
        token.setExpiryDate( Instant.now().plus(AppContants.EMAIL_VERIFY_TOKEN_DURATION, ChronoUnit.MINUTES));
        token.setAttemptCount(1);
        tokenRepository.save(token);
        redisOps.addVerification(simpleToken, token);
        return generateVerificationLink(simpleToken);
    }

    public String generateAttemptToken(Long userID, TokenEntity oldToken){
        String simpleToken = generateToken();
//        oldToken.setToken(hashedToken(simpleToken));
        oldToken.setToken(simpleToken);
        oldToken.setUserId(userID);
        oldToken.setExpiryDate( Instant.now().plus(AppContants.EMAIL_VERIFY_TOKEN_DURATION, ChronoUnit.MINUTES));
        oldToken.setAttemptCount(oldToken.getAttemptCount() + 1);
        oldToken.setUpdatedAt(Instant.now());
        tokenRepository.save(oldToken);
        redisOps.addVerification(simpleToken, oldToken);
        return generateVerificationLink(simpleToken);
    }

    public String getAttemptedVerificationLink(Long userID){
        // Try redis else query DB
        Optional<TokenEntity> tokenVerification = tokenRepository.findByUserId(userID);
        // if currentTime >= updatedAt + 5min == createNewToken else Wait for sometime like 5min
        if(tokenVerification.isPresent()){
            TokenEntity lastToken = tokenVerification.get();
            Instant nextAllowedTime = lastToken.getUpdatedAt().plus(AppContants.RESEND_COOLDOWN_MINUTES, ChronoUnit.MINUTES);
//            Instant nextAllowedTime = lastToken.getUpdatedAt().plus(AppContants.RESEND_COOLDOWN_MINUTES, ChronoUnit.SECONDS);
            if(Instant.now().isBefore(nextAllowedTime)){
                long minutesLeft = Duration.between(Instant.now(), nextAllowedTime).get(ChronoUnit.SECONDS);
                throw new RateLimitException("Please wait " + minutesLeft + " seconds before requesting a new code.");
            }else {
                if( tokenVerification.get().getAttemptCount() <= 5){
                    return generateAttemptToken(userID,tokenVerification.get());
                }else {
                    throw new RuntimeException("Token creation attempt Exhausted. Try With New EMAIL ID");
                }
            }
        }
        return new String("");
    }

    public String generateVerificationLink(String token){
        return AppContants.VERIFICATION_LINK_URI + token;
    }
}//EC
