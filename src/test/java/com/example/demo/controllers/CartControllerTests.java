package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest

public class CartControllerTests {

    private CartController cartController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController(null, null, null);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);
        when(userRepo.findByUsername("test")).thenReturn(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("A widget that is round");
        when(itemRepo.findById(1L)).thenReturn(java.util.Optional.of(item));

    }

    @Test
    public void add_to_cart_success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");
        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        Assert.assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());

    }

    @Test
    public void add_to_cart_invalid_user() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("boo");
        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void add_to_cart_invalid_item() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(2L);
        request.setQuantity(1);
        request.setUsername("test");
        ResponseEntity<Cart> response = cartController.addToCart(request);

        assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_success() {
        // Set up test by adding two items to cart.
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(2);
        request.setUsername("test");
        ResponseEntity<Cart> response = cartController.addToCart(request);
        assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");
        response = cartController.removeFromCart(request);

        assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        Assert.assertEquals(BigDecimal.valueOf(2.99), cart.getTotal());

    }

    @Test
    public void remove_from_cart_invalid_user() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("boo");
        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_invalid_item() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(2L);
        request.setQuantity(1);
        request.setUsername("test");
        ResponseEntity<Cart> response = cartController.removeFromCart(request);

        assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }
}
