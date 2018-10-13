package it.alessandrotorre.spring.mvc.data.rest;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;




@Controller
public class UserController {

    private final static Logger LOGGER = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserRepository userRepository;
    
     @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listUsers(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("users", userRepository.findAll());
        return "users"; 
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addUser(@ModelAttribute("user") User user, BindingResult result) {
        userRepository.save(user);
        return "redirect:/";
    }

    @RequestMapping("/delete/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId) {
        userRepository.delete(userRepository.findOne(userId));
        return "redirect:/";
    }

    /**
     * * ******************* JSON Call ****************
     */
    /*
     curl -X POST -H "Content-Type:application/json" -d "{\"firstName\": \"Mario\", \"lastName\": \"Bianchi\", \"email\": \"test@libero.it\"}" http://localhost:8080/springmvcdatarest/api/users
     */
    @RequestMapping(value = "api/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addUserJson(@RequestBody String json,HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        JSONObject obj = new JSONObject(json);
        User user = getObject(json, User.class);
        if (user != null) {
            userRepository.save(user);
            System.out.println(user.toString());
            return json;
        } else {
            return "{\"Result\": \"Ko\"}";
        }
    }

    /*
     curl -X PUT -H "Content-Type:application/json" -d "{\"id\": \"1\",  \"firstName\": \"cavallo\", \"lastName\": \"Bianchi\", \"email\": \"test@libero.it\"}" http://localhost:8080/springmvcdatarest/api/users/1
     */
    @RequestMapping(value = "api/users/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String updateUserJson(@RequestBody String json, HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        JSONObject obj = new JSONObject(json);
        User user = getObject(json, User.class);
        if ((user != null) && (userRepository.exists(user.getId()))) {
            userRepository.save(user);
            System.out.println(user.toString());
            return json;
        } else {
            return "{\"Result\": \"Ko\"}";
        }
    }

    /*
     curl -X GET -H "Content-Type:application/json" http://localhost:8080/springmvcdatarest/api/users/1 
     */
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String viewUserJson(@PathVariable("id") long id, HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        User user = userRepository.findOne(id);
        if (user != null) {
            JSONObject userJSON = new JSONObject();
            userJSON.put("id", user.getId());
            userJSON.put("firstName", user.getFirstName());
            userJSON.put("lastName", user.getLastName());
            userJSON.put("email", user.getEmail());
            return userJSON.toString();
        } else {
            return "{\"Result\": \"Ko\"}";
        }
    }

    /* 
    curl -X GET -H "Content-Type:application/json" http://localhost:8080/springmvcdatarest/api/users/ 
    */
    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public @ResponseBody
    String listUsersJson(ModelMap model,HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        JSONArray userArray = new JSONArray();
        for (User user : userRepository.findAll()) {
            JSONObject userJSON = new JSONObject();
            userJSON.put("id", user.getId());
            userJSON.put("firstName", user.getFirstName());
            userJSON.put("lastName", user.getLastName());
            userJSON.put("email", user.getEmail());
            userArray.put(userJSON);
        }
        return userArray.toString();
    }

    /*
     curl -X DELETE -H "Content-Type:application/json"  http://localhost:8080/springmvcdatarest/api/users/1
     */
    @RequestMapping(value = "api/users/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteUserJson(@PathVariable("id") long id, HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        User user = userRepository.findOne(id);
        JSONObject userJSON = null;
        if (user != null) {
            userJSON = new JSONObject();
            userJSON.put("id", user.getId());
            userJSON.put("firstName", user.getFirstName());
            userJSON.put("lastName", user.getLastName());
            userJSON.put("email", user.getEmail());
            userRepository.delete(id);
            return userJSON.toString();
        } else {
            return "{\"Result\": \"Ko\"}";
        }

    }

    /**
     * Converte una stringa json in oggetto java. 
    **/
    public static <T> T getObject(final String jsonString, final Class<T> objectClass) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(jsonString, objectClass);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ERRORE " + e.toString());
        }
        return t;
    }

    
    /* to enable the CORS */
    private void setHeaderResponse(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3628800");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    }
    
    
    
}
