#!/bin/bash

ps -ef | grep jndc | grep -v grep | awk '{print $2}' | xargs kill -9
