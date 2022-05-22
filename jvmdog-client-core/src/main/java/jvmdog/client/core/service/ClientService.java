package jvmdog.client.core.service;

public interface ClientService {
    void attach(String pid);
    void detach(String pid);
}
