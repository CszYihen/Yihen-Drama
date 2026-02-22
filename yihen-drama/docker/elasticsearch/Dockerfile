ARG ES_BASE_IMAGE=docker.1ms.run/library/elasticsearch:8.11.3
FROM ${ES_BASE_IMAGE}

ARG IK_PLUGIN_URL=https://get.infini.cloud/elasticsearch/analysis-ik/8.11.3
ARG PINYIN_PLUGIN_URL=https://get.infini.cloud/elasticsearch/analysis-pinyin/8.11.3

RUN elasticsearch-plugin install --batch "${IK_PLUGIN_URL}" \
    && elasticsearch-plugin install --batch "${PINYIN_PLUGIN_URL}"
