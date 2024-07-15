/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/

package io.openliberty.sample.secret;

import com.ibm.websphere.crypto.PasswordUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.MediaType;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
@Path("/secret")
public class SecretResource {

    private static final String SECRET_PHRASE_KEY = "io_openliberty_sample_secret_secretPhrase";
    private static final String DEFAULT_SECRET_PHRASE = "defaultSecretPhrase";

    @Inject
    @ConfigProperty(name = SECRET_PHRASE_KEY, defaultValue = DEFAULT_SECRET_PHRASE)
    private String encryptedSecretPhrase;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSecretPhrase() {
        if (DEFAULT_SECRET_PHRASE.equals(encryptedSecretPhrase)) {
            String message = String.format("ERROR: The secret phrase was not set. [%s]", encryptedSecretPhrase);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
        try {
            String secretPhrase = PasswordUtil.decode(encryptedSecretPhrase);
            String message = String.format("%s=%s", SECRET_PHRASE_KEY, secretPhrase);
            return Response.ok(message).build();    
        } catch (Exception e) {
            String message = String.format("ERROR: Could not decrypt the secret phrase. [%s]", encryptedSecretPhrase);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
        }
    }

}
