// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.spring.data.cosmostutorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.DirectConnectionConfig;
import com.azure.cosmos.GatewayConnectionConfig;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.spring.data.cosmos.config.AbstractCosmosConfiguration;
import com.azure.spring.data.cosmos.config.CosmosConfig;
import com.azure.spring.data.cosmos.core.ResponseDiagnostics;
import com.azure.spring.data.cosmos.core.ResponseDiagnosticsProcessor;
import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import com.azure.spring.data.cosmos.repository.config.EnableReactiveCosmosRepositories;

@Configuration
@EnableConfigurationProperties(CosmosProperties.class)
@EnableCosmosRepositories
@EnableReactiveCosmosRepositories
@PropertySource("classpath:application.properties")
public class SampleAppConfiguration extends AbstractCosmosConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SampleAppConfiguration.class);

    @Autowired
    private CosmosProperties properties;

    private AzureKeyCredential azureKeyCredential;
    
    @Bean
    public CosmosClientBuilder cosmosClientBuilder() {
        DirectConnectionConfig directConnectionConfig = DirectConnectionConfig.getDefaultConfig();
        return new CosmosClientBuilder()
            .endpoint(properties.getUri())
            .key(properties.getKey())
            .directMode(directConnectionConfig);
    }
    
//    public String getConnectUriFromKeyvault() {
//        SecretClient secretClient = new SecretClientBuilder()
//                .vaultUrl("https://devkv.vault.azure.net/")
//                .credential(new DefaultAzureCredentialBuilder().build())
//                .buildClient();
//        KeyVaultSecret secret = secretClient.getSecret("cosmosKey");
//        return secret.getValue();
//    }
//    @Bean
//    public CosmosClientBuilder getCosmosClientBuilder() {
//        this.azureKeyCredential = new AzureKeyCredential(properties.getKey());
//        DirectConnectionConfig directConnectionConfig = new DirectConnectionConfig();
//        GatewayConnectionConfig gatewayConnectionConfig = new GatewayConnectionConfig();
//        return new CosmosClientBuilder()
//            .endpoint(properties.getUri())
//            .credential(azureKeyCredential)
//            .directMode(directConnectionConfig, gatewayConnectionConfig);
//    }
//    @Bean
//    public CosmosClientBuilder getCosmosClientBuilder() {
//        DirectConnectionConfig directConnectionConfig = DirectConnectionConfig.getDefaultConfig();
//            String temp = getConnectUriFromKeyvault();
//        return new CosmosClientBuilder()
//            .endpoint(properties.getUri())
//            .key(temp)
//            .directMode(directConnectionConfig);
//    }

    @Bean
    public CosmosConfig cosmosConfig() {
        return CosmosConfig.builder()
                           .responseDiagnosticsProcessor(new ResponseDiagnosticsProcessorImplementation())
                           .enableQueryMetrics(properties.isQueryMetricsEnabled())
                           .build();
    }

    @Override
    protected String getDatabaseName() {
        return properties.getDbname();
    }

    private static class ResponseDiagnosticsProcessorImplementation implements ResponseDiagnosticsProcessor {

        @Override
        public void processResponseDiagnostics(@Nullable ResponseDiagnostics responseDiagnostics) {
            logger.info("Response Diagnostics {}", responseDiagnostics);
        }
    }
}
