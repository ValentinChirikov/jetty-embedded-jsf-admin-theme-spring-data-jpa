package by.ese.web.app.beans;

import by.ese.web.app.persistence.model.Customer;
import by.ese.web.app.persistence.repository.CustomerRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Optional;

@Named
@RequestScoped
public class Hello {

    @Inject
    CustomerRepository customerRepository;
    private Customer customer;
    private String name;
    private String message;

    @Transactional
    public void createCustomer() {
        customer = customerRepository.save(new Customer(name, name));
    }

    public void createMessage() {
        message = "Hello, " + name + "!";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getCustomerName() {
        return Optional.ofNullable(customer).orElse(new Customer("EMPTY", "EMPTY")).toString();
    }

}