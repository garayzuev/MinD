package com.horizon.mind;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.horizon.mind.dto.Activity;
import com.horizon.mind.dto.Place;
import com.horizon.mind.dto.User;
import com.horizon.mind.service.db.DataBaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@SpringBootApplication(scanBasePackages = "com.horizon.mind")
public class Application {
    private static final String PATH_TO_USERS = "data/Users.json";

    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        DataBaseService db = context.getBean(DataBaseService.class);
        loadTestData(db);
    }

    private static void loadTestData(DataBaseService db) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(PATH_TO_USERS);
        JsonNode jsonNode = mapper.readTree(resource.getInputStream());
        for (JsonNode node : jsonNode) {
            User user = mapper.convertValue(node, User.class);
            User.UserBuilder userBuilder = user.toBuilder();
            userBuilder.password(node.get("password").asText());
            Set<Activity> activities = new HashSet<>();
            for (Activity activity : user.getPreferredActivities()) {
                //Optional<Activity> existingActivity = getActivityByName(db, activity.getName());
                Activity.ActivityBuilder activityBuilder = activity.toBuilder();
                Set<Place> places = new HashSet<>();
                for (Place place : activity.getPreferredPlaces()) {
                    long id = db.addPlace(place);
                    places.add(db.getPlaceById(id).orElse(null));
                }
                activityBuilder.preferredPlaces(places);
                Activity newActivity = activityBuilder.build();
                long id = db.addActivity(newActivity);
                activities.add(db.getActivityById(id).orElse(null));
            }
            userBuilder.preferredActivities(activities);
            Set<User> friends = new HashSet<>();
            for (JsonNode friend : node.get("friends")) {
                User f = mapper.convertValue(friend, User.class);
                friends.add(db.getUserById(f.getId()).orElse(null));
            }
            userBuilder.friends(friends);
            userBuilder.preferredActivities(activities);
            long id = db.addUser(userBuilder.build());
            System.out.println("User name is " + user.getName() + " id is " + id);
        }
    }

    private static Optional<Activity> getActivityByName(DataBaseService db, String activityName) {
        return db.getAllActivities()
                .parallelStream()
                .filter(a -> a.getName().equals(activityName))
                .findFirst();
    }
}
