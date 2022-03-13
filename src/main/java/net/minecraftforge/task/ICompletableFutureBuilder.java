package net.minecraftforge.task;

import java.util.concurrent.CompletableFuture;

interface ICompletableFutureBuilder {

    CompletableFuture<Void> build();
}
