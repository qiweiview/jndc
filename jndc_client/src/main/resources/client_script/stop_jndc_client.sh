ps -ef | grep hi_im_view | grep -v grep | cut -c 9-15 | xargs kill -s 9
