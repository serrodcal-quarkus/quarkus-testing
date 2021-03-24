package com.serrodcal.resource;

import com.serrodcal.exception.FileNullEmptyException;
import com.serrodcal.service.S3Service;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class S3Resource {

    private static final Logger log = Logger.getLogger(S3Resource.class);

    @Inject
    S3Service s3Service;

    @Route(path = "/s3/upload", methods = HttpMethod.POST, consumes = "multipart/form-data")
    public void uploadFile(RoutingContext rc) {

        log.info("S3Resource.fileUpload()");

        FileUpload fileUpload = rc.fileUploads().iterator().next();

        log.debug("File with name: " + fileUpload.uploadedFileName());

        this.s3Service.uploadFile(fileUpload).subscribe().with(result -> {
            rc.response().setStatusCode(HttpResponseStatus.OK.code()).end();
        }, failure -> {
            log.error(failure.getMessage());

            int status = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();

            if (failure instanceof FileNullEmptyException)
                status = HttpResponseStatus.BAD_REQUEST.code();

            rc.response().setStatusCode(status).end();
        });

    }

}