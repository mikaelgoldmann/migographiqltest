package com.spotify.migo.graphiql;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static graphql.schema.GraphQLObjectType.newObject;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeVisitor;
import graphql.schema.PropertyDataFetcher;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.List;

public class Main {

  static class Prop {
    public int getValue() {
      return 99;
    }
  }

  public static void main(String... args) {
    String schema = "type Query{hello: String}";

    final GraphQLObjectType hello = newObject()
        .name("Query")
        .field(newFieldDefinition().name("hello")
            .argument(GraphQLArgument.newArgument().name("name").type(GraphQLString))
            .dataFetcher(new DataFetcher<Object>() {
              @Override
              public Object get(final DataFetchingEnvironment environment) throws Exception {
                final Object name = environment.getArgument("name");
                return name;
              }
            }).type(GraphQLString))
        .build();

    final GraphQLObjectType phrases = newObject().name("Phrases")
        .field(GraphQLFieldDefinition.newFieldDefinition()
            .name("hello")
            .type(GraphQLList.list(hello))
            .dataFetcher((DataFetcher<List<Phrase>>) environment -> {
              final Object name = environment.getArgument("name");
              return null;
            })
            .build())
        .build();

    final GraphQLSchema graphQLSchema = GraphQLSchema.newSchema()
        .query(hello)
        .build();
    GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();

    System.out.println(build.execute("{hello(name: \"migo\")}").getData().toString());
  }

  private static class Phrase {}
}
