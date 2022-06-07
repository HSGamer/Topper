package me.hsgamer.topper.placeholderleaderboard.config.path;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.path.AdvancedConfigPath;
import me.hsgamer.topper.placeholderleaderboard.block.BlockEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BlockEntryConfigPath extends AdvancedConfigPath<List<String>, List<BlockEntry>> {
    public BlockEntryConfigPath(@NotNull String path, @Nullable List<BlockEntry> def) {
        super(path, def);
    }

    @Override
    public @NotNull List<String> getFromConfig(@NotNull Config config) {
        Object raw = config.get(getPath());
        if (raw == null) {
            return Collections.emptyList();
        } else {
            return CollectionUtils.createStringListFromObject(raw, true);
        }
    }

    @Override
    public @NotNull List<BlockEntry> convert(@NotNull List<String> rawValue) {
        return rawValue.stream().map(BlockEntry::deserialize).collect(Collectors.toList());
    }

    @Override
    public @NotNull List<String> convertToRaw(@NotNull List<BlockEntry> value) {
        return value.stream().map(BlockEntry::serialize).collect(Collectors.toList());
    }
}
