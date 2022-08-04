FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 19.3.6.r11-grl && \
    sdk default java 19.3.6.r11-grl"