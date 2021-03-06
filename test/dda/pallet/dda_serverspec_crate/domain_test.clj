; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.


(ns dda.pallet.dda-serverspec-crate.domain-test
  (:require
    [clojure.test :refer :all]
    [dda.pallet.dda-serverspec-crate.domain :as sut]))

(def domain-config-1
  {:netstat {:sshd {:port "22"}}
   :package {:firefox {:installed? false}}
   :file [{:path "/root"}
          {:path "/etc" :exist? true}
          {:path "/absent" :exist? false}]})

(def domain-config-2
  {:file '({:path "/etc"}
           {:path "/nonexist/sth" :exist? false})})

(deftest test-infra-configuration
  (testing
    "test creation of infra configuration"
      (is (=  {:dda-servertest
                {:netstat-fact nil
                 :package-fact nil
                 :file-fact {:_root {:path "/root"}
                             :_etc {:path "/etc"}
                             :_absent {:path "/absent"}}
                 :netstat-test {:sshd {:port "22"}}
                 :package-test {:firefox {:installed? false}}
                 :file-test {:_root {:exist? true}
                             :_etc {:exist? true},
                             :_absent {:exist? false}}}}
            (sut/infra-configuration domain-config-1)))
      (is (=  {:dda-servertest
                {:file-fact {:_etc {:path "/etc"}
                             :_nonexist_sth {:path "/nonexist/sth"}}
                 :file-test {:_etc {:exist? true},
                             :_nonexist_sth  {:exist? false}}}}
            (sut/infra-configuration domain-config-2)))))
