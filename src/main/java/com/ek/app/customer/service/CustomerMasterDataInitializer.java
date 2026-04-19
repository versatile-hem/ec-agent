package com.ek.app.customer.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ek.app.customer.entity.Customer;
import com.ek.app.customer.repository.CustomerRepository;

@Component
public class CustomerMasterDataInitializer implements ApplicationRunner {

    private final CustomerRepository customerRepository;

    public CustomerMasterDataInitializer(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedIfMissing(customers());
    }

    private void seedIfMissing(List<CustomerSeed> seeds) {
        for (CustomerSeed seed : seeds) {
            if (customerRepository.existsByCpId(seed.cpId())) {
                continue;
            }
            Customer customer = new Customer();
            customer.setCpId(seed.cpId());
            customer.setName(seed.shopName());
            customer.setContactName(seed.contactName());
            customer.setPhone(seed.contactNumber() == null || seed.contactNumber().isBlank() ? "0000000000" : seed.contactNumber());
            customer.setAddressLine1(seed.address());
            customer.setArea(seed.area());
            customer.setLatitude(seed.latitude());
            customer.setLongitude(seed.longitude());
            customerRepository.save(customer);
        }
    }

    private List<CustomerSeed> customers() {
        return List.of(
                new CustomerSeed("GH001", "SSB Toy Zone", "Diksha Gumber", null, null, null, null, null),
                new CustomerSeed("GH002", "Munjal Book Depot", null, null, null, null, null, null),
                new CustomerSeed("GH003", "Mayur Book Center", null, null, null, null, null, null),
                new CustomerSeed("GH004", "Pulkit Sports", null, null, null, null, null, null),
                new CustomerSeed("GH005", "Ahuja Sports", null, null, null, null, null, null),
                new CustomerSeed("GH006", "Shri Lalji Toy Zone", null, null, null, null, null, null),
                new CustomerSeed("GH007", "Toyland", null, null, null, null, null, null),
                new CustomerSeed("GH008", "Papercraft Stationary", null, null, null, null, null, null),
                new CustomerSeed("GH009", "Magical Deals", null, null, null, null, null, null),
                new CustomerSeed("GH010", "Sardar Ji Toys", null, null, null, null, null, null),
                new CustomerSeed("GH011", "Mehta Stationery", null, "9313746116", "44/28 Faridabad", "Sector 28", new BigDecimal("28.434807"), new BigDecimal("77.315612")),
                new CustomerSeed("GH012", "Shree Krishna Stationers", null, "9266434899", "41/28 Faridabad", "Sector 28", null, null),
                new CustomerSeed("GH013", "Aditi's Gallery", null, null, "45-46 Faridabad", "Sector 28", null, null),
                new CustomerSeed("GH014", "Mannat gifts", null, "9899294237", null, "Sector 28", new BigDecimal("28.435344"), new BigDecimal("77.31525")),
                new CustomerSeed("GH015", "Gagan Gift Shop", null, "9654555401", "118/31", "Sector 31", null, null),
                new CustomerSeed("GH016", "SG Sports", null, null, null, null, null, null),
                new CustomerSeed("GH017", "Vishal Stationaory", null, null, null, null, null, null),
                new CustomerSeed("GH018", "CADO Gift Stationary", null, null, null, null, null, null),
                new CustomerSeed("GH019", "Kukreja Gift Shop", null, null, null, null, null, null),
                new CustomerSeed("GH020", "Deepak Book Depot", null, null, null, null, null, null),
                new CustomerSeed("GH021", "Friends Book service", null, null, null, null, null, null),
                new CustomerSeed("GH022", "Batra Stationary", null, null, null, "sector 18", null, null),
                new CustomerSeed("GH023", "Taneja Toys & Stationary", null, null, null, null, null, null),
                new CustomerSeed("GH024", "Euro Kids", null, null, null, "Sector 45", null, null),
                new CustomerSeed("GH025", "SHAM Pustak Bhandar", null, null, null, null, null, null),
                new CustomerSeed("GH026", "Bhatia Binding Works", null, null, null, null, null, null),
                new CustomerSeed("GH027", "Exclusive Gift & Toys", null, null, null, null, null, null),
                new CustomerSeed("GH028", "Jeet Stationary", null, null, null, null, null, null),
                new CustomerSeed("GH029", "Shiv Stationary", null, null, null, null, null, null),
                new CustomerSeed("GH030", "Negi Genral Store", null, null, null, null, null, null),
                new CustomerSeed("GH031", "Jindal Book Depot", null, null, null, null, null, null),
                new CustomerSeed("GH032", "Mangla Book depot", null, null, null, null, null, null),
                new CustomerSeed("GH033", "Anand Pushtak Bhandar", null, null, null, null, null, null));
    }

    private record CustomerSeed(
            String cpId,
            String shopName,
            String contactName,
            String contactNumber,
            String address,
            String area,
            BigDecimal latitude,
            BigDecimal longitude) {
    }
}
