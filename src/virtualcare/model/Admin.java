package virtualcare.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Provider> managedProviders;

    public Admin(String adminID, String name) {
        super(adminID, name);
        this.managedProviders = new ArrayList<>();
    }

    public void createProviderAccount(String providerID, String name, String specialty, String availability) {
        Provider provider = new Provider(providerID, name, specialty, availability);
        managedProviders.add(provider);
    }

    public void manageSystem() {
        System.out.println("Admin " + this.name + " is managing the system.");
    }

    public List<Provider> getManagedProviders() {
        return new ArrayList<>(managedProviders);
    }

    public void addProvider(Provider provider) {
        if (provider != null && !managedProviders.contains(provider)) {
            managedProviders.add(provider);
        }
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", managedProviders=" + managedProviders.size() +
                '}';
    }
}

