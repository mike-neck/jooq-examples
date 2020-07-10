package db.fixture;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.support.TransactionOperations;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

public class TestSetup {

    @NotNull private final DSLContext dsl;
    @NotNull private final TransactionOperations tx;
    @NotNull private final JdbcOperations operations;

    public TestSetup(
            @NotNull DSLContext dsl,
            @NotNull TransactionOperations tx,
            @NotNull JdbcOperations operations) {
        this.dsl = dsl;
        this.tx = tx;
        this.operations = operations;
    }

    public void clean() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream stream = classLoader.getResourceAsStream("clean.sql");
        if (stream == null) {
            throw new IllegalStateException("clean.sql not found");
        }
        tx.executeWithoutResult(transactionStatus -> {
            try (Stream<String> sqls = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines()) {
                sqls.filter(it -> !it.isEmpty())
                        .filter(it -> !it.startsWith("--"))
                        .forEach(operations::update);
            }
        });
    }

    @NotNull
    public <T extends UpdatableRecord<T>> Optional<T> insert(@NotNull T record) {
        Optional<T> o = tx.execute(status -> {
            System.out.println(status.isNewTransaction());
            int count = dsl.insertInto(record.getTable())
                    .values(record.intoMap().values().toArray())
                    .execute();
            if (count != 1) {
                return Optional.empty();
            }
            return Optional.of(record);
        });
        return Optional.ofNullable(o).flatMap(it -> it);
    }
}
