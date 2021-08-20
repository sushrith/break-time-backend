package com.example.demo.notify;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.criteria.CriteriaBuilder;

import com.example.demo.Application;
import com.example.demo.model.Interest;
import com.example.demo.model.User_Subscription;
import com.example.demo.notify.dto.*;
import com.example.demo.repository.InterestRepo;
import com.example.demo.repository.UserInterestRepo;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.User_Subscription_Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")
public class PushController {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private InterestRepo interest_repo;

  @Autowired
  private UserInterestRepo userinterest_repo;
  @Autowired
  private User_Subscription_Repo user_subscription_repo;


  private final ServerKeys serverKeys;

  private final CryptoService cryptoService;

  private final Map<String, Subscription> normalSubscriptions = new ConcurrentHashMap<>();

  private final Map<String, Subscription> busySubscriptions = new ConcurrentHashMap<>();

  private final Map<String, Subscription> hecticSubscriptions = new ConcurrentHashMap<>();
  
  

  private final HttpClient httpClient;

  private final Algorithm jwtAlgorithm;

  private final ObjectMapper objectMapper;

  public PushController(ServerKeys serverKeys, CryptoService cryptoService,
                        ObjectMapper objectMapper) {
    this.serverKeys = serverKeys;
    this.cryptoService = cryptoService;
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = objectMapper;

    this.jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(),
            this.serverKeys.getPrivateKey());
  }


  @GetMapping(path = "/publicSigningKey", produces = "application/octet-stream")
  public byte[] publicSigningKey() {
    return this.serverKeys.getPublicKeyUncompressed();
  }

 
  @GetMapping(path = "/publicSigningKeyBase64")
  public String publicSigningKeyBase64() {
    return this.serverKeys.getPublicKeyBase64();
  }

 
  @PostMapping("/subscribe")
  @ResponseStatus(HttpStatus.CREATED)
  public void subscribe(@RequestBody SubscriptionMapper subscriptionmap) {
//    //System.out.println("Subscribed! " + subscriptionmap.getSubscription());

    if(subscriptionmap.getSchedule().equals("0") ){
      this.normalSubscriptions.put(subscriptionmap.getSubscription().getEndpoint(), subscriptionmap.getSubscription());
    }
    else if (subscriptionmap.getSchedule().equals("1") ) {
      //System.out.println("Notification busy Added to Map");
      this.busySubscriptions.put(subscriptionmap.getSubscription().getEndpoint(), subscriptionmap.getSubscription());
    }
    else if(subscriptionmap.getSchedule().equals("2")){
      this.busySubscriptions.put(subscriptionmap.getSubscription().getEndpoint(), subscriptionmap.getSubscription());
    }
    //System.out.println(subscriptionmap.getUsername()+ subscriptionmap.getSchedule());

    int x = userRepository.findIdByEmail(subscriptionmap.getUsername());
    ////System.out.println("here id is "+x);
    List<String> subscriptions=	user_subscription_repo.findSubscriptionsById(x);
    if(subscriptions.size()<=5)
    {
      User_Subscription us1 = new User_Subscription(x,subscriptionmap.getSubscription().getEndpoint());
      user_subscription_repo.save(us1);
    }
  }

 
  @PostMapping("/activityCompleted")
  public void activityCompleted(@RequestBody Subscription subscription) {
    //System.out.println("Activity Completed! "+ subscription.getEndpoint());
//    this.subscriptions.put(subscription.getEndpoint(), subscription);
  }

 
  @PostMapping("/activitySkipped")
  public void activitySkipped(@RequestBody Subscription subscription) {
    //System.out.println("Activity Ignored! "+ subscription.getEndpoint());
//    this.subscriptions.put(subscription.getEndpoint(), subscription);
  }

 

 
  @PostMapping("/unsubscribe")
  public void unsubscribe(@RequestBody SubscriptionEndpoint subscription) {

    if(this.normalSubscriptions.containsKey(subscription.getEndpoint())){
      this.normalSubscriptions.remove(subscription.getEndpoint());
    }
    else if(this.busySubscriptions.containsKey(subscription.getEndpoint())){
      this.busySubscriptions.remove(subscription.getEndpoint());
    }
    else if(this.hecticSubscriptions.containsKey(subscription.getEndpoint())){
      this.hecticSubscriptions.remove(subscription.getEndpoint());
    }

    //System.out.println(subscription.getEndpoint());
    int x = user_subscription_repo.findIdByLink(subscription.getEndpoint());
    //System.out.println(x);
    List<User_Subscription> str=user_subscription_repo.findSubscriptionsByIdUser(x);
    ////System.out.println(str);
    for(User_Subscription u : str){
      //System.out.println(u);
      user_subscription_repo.delete(u);
    }
  }

 
  @PostMapping("/isSubscribed")
  public boolean isSubscribed(@RequestBody SubscriptionEndpoint subscription) {
    return this.normalSubscriptions.containsKey(subscription.getEndpoint())
    ||this.busySubscriptions.containsKey(subscription.getEndpoint())
    || this.hecticSubscriptions.containsKey(subscription.getEndpoint());

  }


  public PushMessage getNotificationData(int userId){
        List<Integer> list =userinterest_repo.findUserIds(userId);
        Random random=new Random();
        int i_id= list.get(random.nextInt(list.size()));
        Interest interest=interest_repo.findByInterestId(i_id);
        String iconUrl= "https://image.flaticon.com/icons/png/512/2534/2534929.png";
        return new PushMessage(interest.getSubtitle(),interest.getDescription(),interest.getUrl(),"relief.png");
  }

  // <div>Icons made by <a href="https://www.flaticon.com/authors/flat-icons" title="Flat Icons">Flat Icons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

  private void sendMessageToSubscriber(Subscription subscription, Object message)throws JsonProcessingException{
    try {
      byte[] result = this.cryptoService.encrypt(
              this.objectMapper.writeValueAsString(message),
              subscription.getKeys().getP256dh(), subscription.getKeys().getAuth(), 0);
      boolean remove = sendPushMessage(subscription, result);

    }
    catch (InvalidKeyException | NoSuchAlgorithmException
            | InvalidAlgorithmParameterException | IllegalStateException
            | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException
            | BadPaddingException e) {
      Application.logger.error("send encrypted message", e);
    }
  }

  @Scheduled(cron = "0/29 * * * * MON-SUN")
  public void normalNotify(){
    if (this.normalSubscriptions.isEmpty() ) {
      return;
    }

    for (Subscription subscription : normalSubscriptions.values()){
      PushMessage message = getNotificationData(user_subscription_repo.findIdByLink(subscription.getEndpoint()));
      try {
        sendMessageToSubscriber(subscription,message);
      }catch (Exception e){
        Application.logger.error("send encrypted message", e);
      }
    }
    System.err.println("Notification normal"+ new Timestamp(new Date().getTime()));

  }
  @Scheduled(cron = "0/19 * * * * MON-SUN")
  public void busyNotify(){

    if (this.busySubscriptions.isEmpty() ) {
      return;
    }

    for (Subscription subscription : busySubscriptions.values()){
        PushMessage message = getNotificationData(user_subscription_repo.findIdByLink(subscription.getEndpoint()));
       try {
         sendMessageToSubscriber(subscription,message);
       }catch (Exception e){
         Application.logger.error("send encrypted message", e);
       }
    }
    System.err.println("Notification busy"+ new Timestamp(new Date().getTime()));

  }

  //@Scheduled(cron = "0/9 * 17-19 * * MON-SUN")
  @Scheduled(cron="0/9 * * * * ?")
  public void hecticNotify(){
    if (this.hecticSubscriptions.isEmpty() ) {
      return;
    }
    for (Subscription subscription : hecticSubscriptions.values()){
      PushMessage message = getNotificationData(user_subscription_repo.findIdByLink(subscription.getEndpoint()));
      try {
        sendMessageToSubscriber(subscription,message);
      }catch (Exception e){
        Application.logger.error("send encrypted message", e);
      }
    }

    System.err.println("Notification hectic"+new Timestamp(new Date().getTime()));
  }


  /**
   * @return true if the subscription is no longer valid and can be removed, false if
   * everything is okay
   */
  private boolean sendPushMessage(Subscription subscription, byte[] body) {
    String origin = null;
    try {
      URL url = new URL(subscription.getEndpoint());
      origin = url.getProtocol() + "://" + url.getHost();
    }
    catch (MalformedURLException e) {
      Application.logger.error("create origin", e);
      return true;
    }

    Date today = new Date();
    Date expires = new Date(today.getTime() + 12 * 60 * 60 * 1000);

    String token = JWT.create().withAudience(origin).withExpiresAt(expires)
            .withSubject("mailto:example@example.com").sign(this.jwtAlgorithm);

    URI endpointURI = URI.create(subscription.getEndpoint());

    Builder httpRequestBuilder = HttpRequest.newBuilder();
    if (body != null) {
      httpRequestBuilder.POST(BodyPublishers.ofByteArray(body))
              .header("Content-Type", "application/octet-stream")
              .header("Content-Encoding", "aes128gcm");
    }
    else {
      httpRequestBuilder.POST(BodyPublishers.ofString(""));
      // httpRequestBuilder.header("Content-Length", "0");
    }

    HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
            .header("Authorization",
                    "vapid t=" + token + ", k=" + this.serverKeys.getPublicKeyBase64())
            .build();
    try {
      HttpResponse<Void> response = this.httpClient.send(request,
              BodyHandlers.discarding());

      switch (response.statusCode()) {
        case 201:
          Application.logger.info("Push message successfully sent: {}",
                  subscription.getEndpoint());
          break;
        case 404:
        case 410:
          Application.logger.warn("Subscription not found or gone: {}",
                  subscription.getEndpoint());
          // remove subscription from our collection of subscriptions
          return true;
        case 429:
          Application.logger.error("Too many requests: {}", request);
          break;
        case 400:
          Application.logger.error("Invalid request: {}", request);
          break;
        case 413:
          Application.logger.error("Payload size too large: {}", request);
          break;
        default:
          Application.logger.error("Unhandled status code: {} / {}", response.statusCode(),
                  request);
      }
    }
    catch (IOException | InterruptedException e) {
      Application.logger.error("send push message", e);
    }

    return false;
  }

}
