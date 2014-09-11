package br.com.example.springoauthclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import java.net.URI;

@Service
class DefaultServiceImpl implements DefaultService {

    @Autowired
    private RestOperations rest;

    @Override
    public String get() {
        return rest.getForObject(URI.create("http://resource.example.com:28080/get"), String.class);
    }

    @Override
    public String list() {
        return rest.getForObject(URI.create("http://resource.example.com:28080/list"), String.class);
    }
}
