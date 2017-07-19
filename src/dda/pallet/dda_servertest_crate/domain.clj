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

(ns dda.pallet.dda-servertest-crate.domain
  (:require
   [schema.core :as s]
   [org.domaindrivenarchitecture.config.commons.map-utils :as map-utils]
   [dda.pallet.dda-servertest-crate.infra :as infra]))

(def ServerTestDomainConfig
 {(s/optional-key :package) {s/Keyword {:installed? s/Bool}}
  (s/optional-key :netstat) {s/Keyword {:port s/Str}}
  (s/optional-key :file) [{:path s/Str
                           (s/optional-key :exist?) s/Bool}]})

(defn- path-2-key [path]
  (keyword (clojure.string/replace path "/" "_")))

(defn- domain-2-filefacts [file-domain-config]
  (apply merge
    (map
     #(let [path (:path %)] {(path-2-key path) {:path path}})
     file-domain-config)))

(defn- domain-2-filetests [file-domain-config]
 (apply merge
   (map
    #(let [{:keys [path exists?] :or {exists? true}} %]
          {(path-2-key path) {:exists? exists?}})
    file-domain-config)))

(s/defn ^:always-validate infra-configuration
 [domain-config :- ServerTestDomainConfig]
 (let [{:keys [file package netstat]} domain-config]
  {infra/facility
   (merge
    (if (contains? domain-config :package)
        {:package-fact nil
         :package-test package}
        {})
    (if (contains? domain-config :netstat)
        {:netstat-fact nil
         :netstat-test netstat}
        {})
    (if (contains? domain-config :file)
        {:file-fact (domain-2-filefacts file)
         :file-test (domain-2-filetests file)}
        {}))}))
