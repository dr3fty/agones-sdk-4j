package dev.agones.sdk;

import dev.agones.sdk.Sdk;
import dev.agones.sdk.alpha.Alpha;

import java.util.List;
import java.util.function.Function;

class AgonesMappers {

    static final Function<Sdk.GameServer, AgonesGameServer> GAME_SERVER_MAPPER = AgonesGameServer::fromProto;

    static final Function<Alpha.Bool, Boolean> ALPHA_BOOL_MAPPER = Alpha.Bool::getBool;

    static final Function<Alpha.Count, Long> ALPHA_COUNT_MAPPER = Alpha.Count::getCount;

    static final Function<Alpha.PlayerIDList, List<String>> ALPHA_PLAYER_ID_LIST_MAPPER  = Alpha.PlayerIDList::getListList;
}
