package com.example.demo.services;

import com.example.demo.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class NoyauSihService extends ApplicationService{
    private final String casUrl = "https://cas.chu-reims.fr";
    private final String noyauSihUrl = "http://noyausih.domchurs.ad";
    private final String responseCasOk = "yes";
    private final String applicationCI = "CI0841";

    public NoyauSihService() {}

    public NoyauResponseCas verifyTicketFromCas(String ticket, String service) {
        NoyauResponseCas response = new NoyauResponseCas();
        try {
            URL url = new URL(this.casUrl + "/validate?service=" + service + "&ticket=" + ticket);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            int responseHttpCode = connection.getResponseCode();
            HttpUtils.ensureStatusCodeIsOk(responseHttpCode);
            String[] responseHttpStrings = HttpUtils.getConnectionResponseString(connection).split("\n");
            if (!responseHttpStrings[0].equals(this.responseCasOk)) {
                response.addErrors("CAS did not authorize");
                return response;
            }
            response.setIdRes(responseHttpStrings[responseHttpStrings.length - 1]);
            response.setSuccess();
            return response;
        } catch(Exception e) {
            response.addErrors(e.getMessage());
            return response;
        }
    }

    public NoyauResponseUser getUserFromNoyauSih(String userIdRes) {
        NoyauResponseUser response = new NoyauResponseUser();
        try {
            URL url = new URL(this.noyauSihUrl + "/iam/" + userIdRes + "/" + this.applicationCI + "/habilitations.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            int responseHttpCode = connection.getResponseCode();
            HttpUtils.ensureStatusCodeIsOk(responseHttpCode);
            String responseHttpString = HttpUtils.getConnectionResponseString(connection);
            response.setNoyauSihUser(new ObjectMapper().readValue(responseHttpString, NoyauSihUser.class));
            response.setSuccess();
            return response;
        } catch(Exception e) {
            response.addErrors(e.getMessage());
            return response;
        }
    }

    // Service responses
    private static class NoyauResponseCas extends ResponseService {
        public String getIdRes() {
            return idRes;
        }

        public void setIdRes(String idRes) {
            this.idRes = idRes;
        }

        private String idRes;
    }

    private static class NoyauResponseUser extends ResponseService {
        public NoyauSihUser getNoyauSihUser() {
            return noyauSihUser;
        }

        public void setNoyauSihUser(NoyauSihUser noyauSihUser) {
            this.noyauSihUser = noyauSihUser;
        }

        private NoyauSihUser noyauSihUser;
    }

    // Utils
    private static class NoyauSihUser {
        private String id_res;
        private String windows;
        private String personne;

        public String getId_res() {
            return id_res;
        }

        public void setId_res(String id_res) {
            this.id_res = id_res;
        }

        public String getWindows() {
            return windows;
        }

        public void setWindows(String windows) {
            this.windows = windows;
        }

        public String getPersonne() {
            return personne;
        }

        public void setPersonne(String personne) {
            this.personne = personne;
        }

        public String getCourriel() {
            return courriel;
        }

        public void setCourriel(String courriel) {
            this.courriel = courriel;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getProfils() {
            return profils;
        }

        public void setProfils(String profils) {
            this.profils = profils;
        }

        private String courriel;
        private String nom;
        private String prenom;
        private String profils;
    }
}
