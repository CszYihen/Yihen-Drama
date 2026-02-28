<template>
  <div class="workspace">
    <header class="workspace-header">
      <div class="header-left">
        <button class="back-btn" @click="$router.push('/')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="19" y1="12" x2="5" y2="12"/>
            <polyline points="12 19 5 12 12 5"/>
          </svg>
        </button>
        <div class="project-title">
          <h1 class="title-h2">{{ projectName }}</h1>
          <span class="project-episodes">{{ episodes.length }}集</span>
        </div>
      </div>
      <div class="header-right">
        <div class="consistency-badge" v-if="consistency > 0">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
          </svg>
          {{ consistency }}% 一致性
        </div>
        <button class="btn btn-secondary">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
            <circle cx="12" cy="12" r="1"/>
            <circle cx="19" cy="12" r="1"/>
            <circle cx="5" cy="12" r="1"/>
          </svg>
        </button>
      </div>
    </header>
    
    <div class="workspace-content">
      <!-- 左侧：章节列表 -->
      <aside class="chapters-sidebar" :class="{ collapsed: leftPanelCollapsed }">
        <div class="sidebar-header">
          <h3 class="sidebar-title" v-if="!leftPanelCollapsed">章节列表</h3>
          <button class="add-chapter-btn" v-if="!leftPanelCollapsed" @click="addChapter">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"/>
              <line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
          </button>
        </div>
        
        <div class="chapters-list" v-if="!leftPanelCollapsed">
          <div 
            v-for="(episode, index) in episodes" 
            :key="episode.id"
            class="chapter-item"
            :class="{ active: activeEpisode === episode.id }"
            @click="selectEpisode(episode.id)"
          >
            <div class="chapter-number">#{{ index + 1 }}</div>
            <div class="chapter-info">
              <span class="chapter-name">{{ episode.name }}</span>
              <span class="chapter-status" :class="statusClass[episode.status]">{{ statusText[episode.status] }}</span>
            </div>
            <button class="chapter-delete-btn" @click.stop="handleDeleteEpisode(episode.id)" title="删除章节">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
        </div>

      </aside>
      
      <!-- 中间：主内容区 -->
      <main class="main-panel">
        <div class="main-header">
          <div class="workflow-progress">
            <div 
              v-for="(step, index) in workflowSteps" 
              :key="step.id"
              class="workflow-step"
              :class="{ active: currentStepNum >= index, completed: currentStepNum > index, clickable: !!activeEpisode && index <= maxReachedStep }"
              @click="goToStep(index)"
            >
              <div class="step-indicator">
                <span class="step-number" v-if="currentStepNum <= index">{{ index + 1 }}</span>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
              </div>
              <span class="step-label">{{ step.name }}</span>
            </div>
          </div>
          
          <div class="step-actions-header">
            <span class="save-status" :class="saveStatus.value">{{ saveStatusText }}</span>
          </div>
        </div>
        
        <div class="content-area">
          <div v-if="!activeEpisode" class="empty-state">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="12" y1="18" x2="12" y2="12"/>
              <line x1="9" y1="15" x2="15" y2="15"/>
            </svg>
            <p>选择一个章节开始创作</p>
          </div>
          
          <template v-else>
            <!-- 步骤0: 输入小说内容 -->
            <div v-show="currentStepNum === 0" class="step-content">
              <div class="editor-container">
                <div class="editor-header">
                  <h3 class="title-h3">{{ currentEpisodeName }}</h3>
                  <div class="editor-actions">
                    <button class="toolbar-btn" title="更新章节信息" @click="openUpdateEpisodeModal">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 20h9"/>
                        <path d="M16.5 3.5a2.121 2.121 0 1 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/>
                      </svg>
                      更新章节
                    </button>
                    <button class="toolbar-btn" title="保存" @click="saveEpisode">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                        <polyline points="17 21 17 13 7 13 7 21"/>
                        <polyline points="7 3 7 8 15 8"/>
                      </svg>
                      保存
                    </button>
                  </div>
                </div>
                <textarea 
                  class="editor-textarea" 
                  v-model="currentContent"
                  placeholder="在此粘贴小说章节内容..."
                ></textarea>
                <div class="editor-footer">
                  <span class="word-count">{{ currentContent.length }} 字</span>
                  <div class="footer-actions">
                    <button 
                      v-if="hasExtractedInfo" 
                      class="btn btn-secondary" 
                      @click="skipExtract"
                    >
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="9 18 15 12 9 6"/>
                      </svg>
                      跳过提取
                    </button>
                    <button class="btn btn-primary" @click="nextStep" :disabled="!currentContent.trim()">
                      开始提取
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="5" y1="12" x2="19" y2="12"/>
                        <polyline points="12 5 19 12 12 19"/>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 步骤1: 提取关键信息 -->
            <div v-show="currentStepNum === 1" class="step-content extract-step">
              <div class="extract-header">
                <h3 class="title-h3">提取结果</h3>
                <button class="btn btn-secondary" @click="reExtract" :disabled="extracting">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="23 4 23 10 17 10"/>
                    <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/>
                  </svg>
                  重新提取
                </button>
              </div>
              
              <div class="extraction-loading" v-if="extracting">
                <div class="extraction-spinner"></div>
                <p>正在分析文本内容...</p>
              </div>
              
              <div class="extraction-content" v-else>
                <div class="extraction-grid">
                  <div class="extraction-section">
                    <div class="section-header">
                      <h4>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                          <circle cx="12" cy="7" r="4"/>
                        </svg>
                        人物角色 ({{ extractedInfo.characters.length }})
                      </h4>
                    </div>
                    <div class="extract-characters-list" v-if="extractedInfo.characters.length > 0">
                      <div 
                        v-for="char in extractedInfo.characters" 
                        :key="char.id"
                        class="extract-character-item"
                      >
                        <div class="extract-avatar">
                          <img :src="char.avatar" :alt="char.name" />
                        </div>
                        <div class="extract-info">
                          <span class="extract-name">{{ char.name }}</span>
                          <span class="extract-desc">{{ char.description }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="empty-section" v-else>
                      <p>暂未提取到角色信息</p>
                    </div>
                  </div>
                  
                  <div class="extraction-section">
                    <div class="section-header">
                      <h4>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                          <circle cx="8.5" cy="8.5" r="1.5"/>
                          <polyline points="21 15 16 10 5 21"/>
                        </svg>
                        场景列表 ({{ extractedInfo.scenes.length }})
                      </h4>
                    </div>
                    <div class="scenes-list" v-if="extractedInfo.scenes.length > 0">
                      <div 
                        v-for="scene in extractedInfo.scenes" 
                        :key="scene.id"
                        class="scene-item"
                      >
                        <div class="scene-icon">
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="3" width="18" height="18" rx="2"/>
                          </svg>
                        </div>
                        <span>{{ scene.name }}</span>
                      </div>
                    </div>
                    <div class="empty-section" v-else>
                      <p>暂未提取到场景信息</p>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="step-actions" v-if="!extracting">
                <button class="btn btn-secondary" @click="prevStep">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="19" y1="12" x2="5" y2="12"/>
                    <polyline points="12 19 5 12 12 5"/>
                  </svg>
                  上一步
                </button>
                <button class="btn btn-primary" @click="nextStep">
                  生成关键图片
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="5" y1="12" x2="19" y2="12"/>
                    <polyline points="12 5 19 12 12 19"/>
                  </svg>
                </button>
              </div>
            </div>

            <!-- 步骤2: 生成关键图片 -->
            <div v-show="currentStepNum === 2" class="step-content">
              <h3 class="title-h3">生成关键图片</h3>
              <p class="step-desc">为角色和场景生成参考图片，确保后续视频的一致性</p>

              <div class="image-generation">
                <div class="generation-tabs">
                  <button
                    v-for="tab in imageTabs"
                    :key="tab"
                    class="gen-tab"
                    :class="{ active: imageTab === tab }"
                    @click="imageTab = tab"
                  >
                    {{ tab }}
                  </button>
                </div>

                <!-- 人物标签页 -->
                <div v-show="imageTab === '人物'" class="character-generation">
                  <!-- 本章人物 -->
                  <div class="generation-section" v-if="extractedInfo.characters.length > 0">
                    <div class="section-title-row">
                      <h4 class="section-title">本章人物</h4>
                      <div class="section-header-actions">
                        <button class="btn btn-secondary btn-sm add-character-btn" @click="generateAllCharacters">
                          一键生成
                        </button>
                        <button
                          class="btn btn-secondary btn-sm add-character-btn"
                          @click="generateSelectedCharacters"
                          :disabled="selectedCharacterCount === 0"
                        >
                          批量生成({{ selectedCharacterCount }})
                        </button>
                        <button class="btn btn-primary btn-sm add-character-btn" @click="openAddCharacterDrawer">
                          添加角色
                        </button>
                      </div>
                    </div>
                    <div class="character-grid">
                      <div
                        v-for="character in extractedInfo.characters"
                        :key="character.id"
                        class="character-card"
                        :class="{ selectedForBatch: isCharacterSelected(character.id) }"
                      >
                        <button
                          class="card-select-toggle"
                          type="button"
                          @click.stop="toggleCharacterSelection(character.id)"
                          :title="isCharacterSelected(character.id) ? '取消勾选' : '勾选用于批量生成'"
                        >
                          <svg v-if="isCharacterSelected(character.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="20 6 9 17 4 12"/>
                          </svg>
                        </button>
                        <div 
                          class="character-avatar" 
                          v-if="character.avatar"
                          @dragover.prevent="handleCharacterDragOver(character)"
                          @dragleave.prevent="handleCharacterDragLeave(character)"
                          @drop.prevent="handleCharacterDrop(character, $event)"
                        >
                          <div class="avatar-actions-left">
                            <button 
                              class="icon-btn delete-character-btn" 
                              :disabled="deletingCharacters.has(character.id)"
                              @click.stop="openDeleteConfirm(character)"
                              title="删除角色"
                            >
                              <svg v-if="!deletingCharacters.has(character.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="18" y1="6" x2="6" y2="18"/>
                                <line x1="6" y1="6" x2="18" y2="18"/>
                              </svg>
                              <svg v-else class="spinner" viewBox="0 0 24 24" width="16" height="16">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                                </circle>
                              </svg>
                            </button>
                          </div>
                          <div class="avatar-actions-right">
                            <button
                              class="icon-btn upload-scene-btn"
                              :disabled="uploadingCharacters.has(character.id)"
                              @click.stop="triggerCharacterUpload(character.id)"
                              title="上传角色图片"
                            >
                              <svg v-if="!uploadingCharacters.has(character.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                <polyline points="17 8 12 3 7 8"/>
                                <line x1="12" y1="3" x2="12" y2="15"/>
                              </svg>
                              <svg v-else class="spinner" viewBox="0 0 24 24" width="16" height="16">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                                </circle>
                              </svg>
                            </button>
                            <input
                              class="scene-upload-input"
                              type="file"
                              accept="image/*"
                              :id="`character-upload-${character.id}`"
                              @change="handleCharacterUpload(character, $event)"
                            />
                          </div>
                          <img
                            :src="character.avatar"
                            :alt="character.name"
                            class="character-image"
                            @click.stop="openImagePreview(character.avatar, character.name)"
                          />
                          <div class="scene-drag-hint" v-if="draggingCharacters.has(character.id)">
                            拖拽图片上传
                          </div>
                          <div class="avatar-actions">
                            <button class="avatar-action-btn" @click.stop="downloadImage(character.avatar, character.name)" title="下载图片">
                              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                <polyline points="7 10 12 15 17 10"/>
                                <line x1="12" y1="15" x2="12" y2="3"/>
                              </svg>
                            </button>
                          </div>
                        </div>
                        <div 
                          v-else 
                          class="character-avatar"
                          @dragover.prevent="handleCharacterDragOver(character)"
                          @dragleave.prevent="handleCharacterDragLeave(character)"
                          @drop.prevent="handleCharacterDrop(character, $event)"
                        >
                          <div class="avatar-actions-left">
                            <button 
                              class="icon-btn delete-character-btn" 
                              :disabled="deletingCharacters.has(character.id)"
                              @click.stop="openDeleteConfirm(character)"
                              title="删除角色"
                            >
                              <svg v-if="!deletingCharacters.has(character.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="18" y1="6" x2="6" y2="18"/>
                                <line x1="6" y1="6" x2="18" y2="18"/>
                              </svg>
                              <svg v-else class="spinner" viewBox="0 0 24 24" width="16" height="16">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                                </circle>
                              </svg>
                            </button>
                          </div>
                          <div class="avatar-actions-right">
                            <button
                              class="icon-btn upload-scene-btn"
                              :disabled="uploadingCharacters.has(character.id)"
                              @click.stop="triggerCharacterUpload(character.id)"
                              title="上传角色图片"
                            >
                              <svg v-if="!uploadingCharacters.has(character.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                <polyline points="17 8 12 3 7 8"/>
                                <line x1="12" y1="3" x2="12" y2="15"/>
                              </svg>
                              <svg v-else class="spinner" viewBox="0 0 24 24" width="16" height="16">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                                </circle>
                              </svg>
                            </button>
                            <input
                              class="scene-upload-input"
                              type="file"
                              accept="image/*"
                              :id="`character-upload-${character.id}`"
                              @change="handleCharacterUpload(character, $event)"
                            />
                          </div>
                          <div class="character-placeholder">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                              <circle cx="12" cy="7" r="4"/>
                            </svg>
                          </div>
                          <div class="scene-drag-hint" v-if="draggingCharacters.has(character.id)">
                            拖拽图片上传
                          </div>
                          <div v-if="generatingCharacter.has(character.id)" class="generating-overlay">
                            <svg class="spinner" viewBox="0 0 24 24" width="32" height="32">
                              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                              </circle>
                            </svg>
                          </div>
                        </div>
                        <div class="character-info">
                          <input 
                            v-model="character.name"
                            class="character-name-input"
                            placeholder="点击编辑角色名称"
                            @blur="saveCharacterInfo(character)"
                          />
                          <textarea 
                            v-model="character.description" 
                            class="character-desc-input"
                            placeholder="点击编辑角色描述"
                            @blur="saveCharacterInfo(character)"
                          ></textarea>
                        </div>
                        <div class="character-actions">
                          <button
                            class="btn btn-sm character-action-btn"
                            @click="generateCharacterImage(character, hasRealAvatar(character) ? 'regenerate' : 'generate')"
                            :disabled="generatingCharacter.has(character.id)"
                          >
                            <svg v-if="generatingCharacter.has(character.id)" class="spinner" viewBox="0 0 24 24" width="14" height="14">
                              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                              </circle>
                            </svg>
                            <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M21 12a9 9 0 1 1-9-9"/>
                              <path d="M21 3v6h-6"/>
                            </svg>
                            {{ hasRealAvatar(character) ? '重新生成' : '生成' }}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 项目人物（生成图片步骤下隐藏） -->
                  <div class="generation-section" v-if="false && projectCharacters.length > 0">
                    <h4 class="section-title">项目人物</h4>
                    <div class="character-grid">
                      <div
                        v-for="character in projectCharacters"
                        :key="character.id"
                        class="character-card"
                      >
                        <div 
                          class="character-avatar" 
                          v-if="character.avatar"
                          @click="openImagePreview(character.avatar, character.name)"
                        >
                          <div class="avatar-actions-left">
                            <button 
                              class="icon-btn delete-character-btn" 
                              :disabled="deletingCharacters.has(character.id)"
                              @click.stop="openDeleteConfirm(character)"
                              title="删除角色"
                            >
                              <svg v-if="!deletingCharacters.has(character.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="18" y1="6" x2="6" y2="18"/>
                                <line x1="6" y1="6" x2="18" y2="18"/>
                              </svg>
                              <svg v-else class="spinner" viewBox="0 0 24 24" width="16" height="16">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                                </circle>
                              </svg>
                            </button>
                          </div>
                          <img
                            :src="character.avatar"
                            :alt="character.name"
                            class="character-image"
                          />
                          <div class="avatar-actions">
                            <button class="avatar-action-btn" @click.stop="downloadImage(character.avatar, character.name)" title="下载图片">
                              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                <polyline points="7 10 12 15 17 10"/>
                                <line x1="12" y1="15" x2="12" y2="3"/>
                              </svg>
                            </button>
                          </div>
                        </div>
                        <div v-else class="character-avatar">
                          <div class="avatar-actions-left">
                            <button 
                              class="icon-btn delete-character-btn" 
                              :disabled="deletingCharacters.has(character.id)"
                              @click.stop="openDeleteConfirm(character)"
                              title="删除角色"
                            >
                              <svg v-if="!deletingCharacters.has(character.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="18" y1="6" x2="6" y2="18"/>
                                <line x1="6" y1="6" x2="18" y2="18"/>
                              </svg>
                              <svg v-else class="spinner" viewBox="0 0 24 24" width="16" height="16">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                                </circle>
                              </svg>
                            </button>
                          </div>
                          <div class="character-placeholder">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                              <circle cx="12" cy="12" r="4"/>
                            </svg>
                          </div>
                          <div v-if="generatingCharacter.has(character.id)" class="generating-overlay">
                            <svg class="spinner" viewBox="0 0 24 24" width="32" height="32">
                              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                              </circle>
                            </svg>
                          </div>
                        </div>
                        <div class="character-info">
                          <input 
                            v-model="character.name"
                            class="character-name-input"
                            placeholder="点击编辑角色名称"
                            @blur="saveCharacterInfo(character)"
                          />
                          <textarea 
                            v-model="character.description" 
                            class="character-desc-input"
                            placeholder="点击编辑角色描述"
                            @blur="saveCharacterInfo(character)"
                          ></textarea>
                        </div>
                        <div class="character-actions">
                          <button
                            class="btn btn-sm character-action-btn"
                            @click="generateCharacterImage(character, hasRealAvatar(character) ? 'regenerate' : 'generate')"
                            :disabled="generatingCharacter.has(character.id)"
                          >
                            <svg v-if="generatingCharacter.has(character.id)" class="spinner" viewBox="0 0 24 24" width="14" height="14">
                              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                              </circle>
                            </svg>
                            <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M21 12a9 9 0 1 1-9-9"/>
                              <path d="M21 3v6h-6"/>
                            </svg>
                            {{ hasRealAvatar(character) ? '重新生成' : '生成' }}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="extractedInfo.characters.length === 0 && projectCharacters.length === 0" class="empty-generation">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                      <circle cx="12" cy="7" r="4"/>
                    </svg>
                    <p>暂无可生成图片的人物</p>
                  </div>
                </div>

                <!-- 场景标签页 -->
                <div v-show="imageTab === '场景'" class="scene-generation">
                  <div class="generation-section" v-if="extractedInfo.scenes.length > 0">
                    <div class="section-title-row">
                      <h4 class="section-title">本章场景</h4>
                      <button class="btn btn-primary btn-sm add-character-btn" @click="openAddSceneDrawer">
                        添加场景
                      </button>
                    </div>
                    <div class="character-grid">
                      <div
                        v-for="scene in extractedInfo.scenes"
                        :key="scene.id"
                        class="character-card scene-card"
                      >
                        <div 
                          class="character-avatar scene-avatar"
                          @dragover.prevent="handleSceneDragOver(scene)"
                          @dragleave.prevent="handleSceneDragLeave(scene)"
                          @drop.prevent="handleSceneDrop(scene, $event)"
                        >
                          <div class="avatar-actions-left">
                            <button 
                              class="icon-btn delete-character-btn" 
                              :disabled="deletingScenes.has(scene.id)"
                              @click.stop="openDeleteSceneConfirm(scene)"
                              title="删除场景"
                            >
                              <svg v-if="!deletingScenes.has(scene.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="18" y1="6" x2="6" y2="18"/>
                                <line x1="6" y1="6" x2="18" y2="18"/>
                              </svg>
                              <svg v-else class="spinner" viewBox="0 0 24 24" width="16" height="16">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                                </circle>
                              </svg>
                            </button>
                          </div>
                          <div class="avatar-actions-right">
                            <button
                              class="icon-btn upload-scene-btn"
                              :disabled="uploadingScene.has(scene.id)"
                              @click.stop="triggerSceneUpload(scene.id)"
                              title="上传场景图片"
                            >
                              <svg v-if="!uploadingScene.has(scene.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                <polyline points="17 8 12 3 7 8"/>
                                <line x1="12" y1="3" x2="12" y2="15"/>
                              </svg>
                              <svg v-else class="spinner" viewBox="0 0 24 24" width="16" height="16">
                                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                  <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                                </circle>
                              </svg>
                            </button>
                            <input
                              class="scene-upload-input"
                              type="file"
                              accept="image/*"
                              :id="`scene-upload-${scene.id}`"
                              @change="handleSceneUpload(scene, $event)"
                            />
                          </div>
                          <img
                            :src="getSceneThumbnail(scene)"
                            :alt="scene.name"
                            class="character-image"
                            @click.stop="openImagePreview(getSceneThumbnail(scene), scene.name)"
                          />
                          <div class="avatar-actions">
                            <button class="avatar-action-btn" @click.stop="downloadImage(getSceneThumbnail(scene), scene.name)" title="下载图片">
                              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                                <polyline points="7 10 12 15 17 10"/>
                                <line x1="12" y1="15" x2="12" y2="3"/>
                              </svg>
                            </button>
                          </div>
                          <div class="scene-drag-hint" v-if="draggingScenes.has(scene.id)">
                            拖拽图片上传
                          </div>
                        </div>
                        <div class="character-info">
                          <input 
                            v-model="scene.name"
                            class="character-name-input"
                            placeholder="点击编辑场景名称"
                            @blur="saveSceneInfo(scene)"
                          />
                          <textarea 
                            v-model="scene.description" 
                            class="character-desc-input"
                            placeholder="点击编辑场景描述"
                            @blur="saveSceneInfo(scene)"
                          ></textarea>
                        </div>
                        <div class="character-actions">
                          <button
                            class="btn btn-sm character-action-btn"
                            @click="generateSceneImage(scene, scene.thumbnail ? 'regenerate' : 'generate')"
                            :disabled="generatingScene.has(scene.id)"
                          >
                            <svg v-if="generatingScene.has(scene.id)" class="spinner" viewBox="0 0 24 24" width="14" height="14">
                              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                                <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                              </circle>
                            </svg>
                            <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M21 12a9 9 0 1 1-9-9"/>
                              <path d="M21 3v6h-6"/>
                            </svg>
                            {{ scene.thumbnail ? '重新生成' : '生成' }}
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div v-if="extractedInfo.scenes.length === 0" class="empty-generation">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                      <circle cx="8.5" cy="8.5" r="1.5"/>
                      <polyline points="21 15 16 10 5 21"/>
                    </svg>
                    <p>暂无可生成图片的场景</p>
                    <button class="btn btn-primary btn-sm add-character-btn" @click="openAddSceneDrawer">
                      添加场景
                    </button>
                  </div>
                </div>
              </div>

              <!-- 添加角色抽屉 -->
              <div class="drawer-overlay" v-if="showAddCharacterDrawer" @click.self="closeAddCharacterDrawer">
                <div class="drawer">
                  <div class="drawer-header">
                    <h4>添加角色</h4>
                    <button class="drawer-close" @click="closeAddCharacterDrawer" aria-label="关闭">
                      ×
                    </button>
                  </div>
                  <div class="drawer-body">
                    <label class="form-label">角色名称 <span class="required">*</span></label>
                    <input
                      v-model="addCharacterForm.name"
                      class="form-input"
                      placeholder="请输入角色名称"
                    />
                    <label class="form-label">角色描述（选填）</label>
                    <textarea
                      v-model="addCharacterForm.description"
                      class="form-textarea"
                      placeholder="请输入角色描述"
                      rows="4"
                    ></textarea>
                    <p class="form-error" v-if="addCharacterError">{{ addCharacterError }}</p>
                  </div>
                  <div class="drawer-footer">
                    <button class="btn btn-secondary" @click="closeAddCharacterDrawer">取消</button>
                    <button class="btn btn-primary" :disabled="addingCharacter" @click="submitAddCharacter">
                      <svg v-if="addingCharacter" class="spinner" viewBox="0 0 24 24" width="16" height="16">
                        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                          <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                        </circle>
                      </svg>
                      确认
                    </button>
                  </div>
                </div>
              </div>

              <!-- 添加场景抽屉 -->
              <div class="drawer-overlay" v-if="showAddSceneDrawer" @click.self="closeAddSceneDrawer">
                <div class="drawer">
                  <div class="drawer-header">
                    <h4>添加场景</h4>
                    <button class="drawer-close" @click="closeAddSceneDrawer" aria-label="关闭">
                      ×
                    </button>
                  </div>
                  <div class="drawer-body">
                    <label class="form-label">场景名称 <span class="required">*</span></label>
                    <input
                      v-model="addSceneForm.name"
                      class="form-input"
                      placeholder="请输入场景名称"
                    />
                    <label class="form-label">场景描述（选填）</label>
                    <textarea
                      v-model="addSceneForm.description"
                      class="form-textarea"
                      placeholder="请输入场景描述"
                      rows="4"
                    ></textarea>
                    <p class="form-error" v-if="addSceneError">{{ addSceneError }}</p>
                  </div>
                  <div class="drawer-footer">
                    <button class="btn btn-secondary" @click="closeAddSceneDrawer">取消</button>
                    <button class="btn btn-primary" :disabled="addingScene" @click="submitAddScene">
                      <svg v-if="addingScene" class="spinner" viewBox="0 0 24 24" width="16" height="16">
                        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                          <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                        </circle>
                      </svg>
                      确认
                    </button>
                  </div>
                </div>
              </div>

              <!-- 删除角色确认弹窗 -->
              <div class="modal-overlay" v-if="deleteConfirm.visible">
                <div class="modal">
                  <div class="modal-header">
                    <h4>确认删除</h4>
                    <button class="modal-close" @click="closeDeleteConfirm" aria-label="关闭">×</button>
                  </div>
                  <div class="modal-body">
                    <p>确定要删除角色「{{ deleteConfirm.character?.name || '' }}」吗？</p>
                    <p class="modal-tip">删除后相关生成任务将被清除。</p>
                    <p class="form-error" v-if="deleteConfirm.error">{{ deleteConfirm.error }}</p>
                  </div>
                  <div class="modal-footer">
                    <button class="btn btn-secondary" @click="closeDeleteConfirm">取消</button>
                    <button class="btn btn-primary" :disabled="deleteConfirm.loading" @click="deleteCharacter">
                      <svg v-if="deleteConfirm.loading" class="spinner" viewBox="0 0 24 24" width="16" height="16">
                        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                          <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                        </circle>
                      </svg>
                      确认删除
                    </button>
                  </div>
                </div>
              </div>

              <!-- 删除场景确认弹窗 -->
              <div class="modal-overlay" v-if="deleteSceneConfirm.visible">
                <div class="modal">
                  <div class="modal-header">
                    <h4>确认删除</h4>
                    <button class="modal-close" @click="closeDeleteSceneConfirm" aria-label="关闭">×</button>
                  </div>
                  <div class="modal-body">
                    <p>确定要删除场景「{{ deleteSceneConfirm.scene?.name || '' }}」吗？</p>
                    <p class="modal-tip">删除后将移除该场景资源。</p>
                    <p class="form-error" v-if="deleteSceneConfirm.error">{{ deleteSceneConfirm.error }}</p>
                  </div>
                  <div class="modal-footer">
                    <button class="btn btn-secondary" @click="closeDeleteSceneConfirm">取消</button>
                    <button class="btn btn-primary" :disabled="deleteSceneConfirm.loading" @click="deleteScene">
                      <svg v-if="deleteSceneConfirm.loading" class="spinner" viewBox="0 0 24 24" width="16" height="16">
                        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                          <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                        </circle>
                      </svg>
                      确认删除
                    </button>
                  </div>
                </div>
              </div>

              <div class="step-actions">
                <button class="btn btn-secondary" @click="prevStep">上一步</button>
                <button class="btn btn-primary" @click="nextStep">角色固定</button>
              </div>
            </div>
            
            <!-- 步骤3: 角色固定 -->
            <div v-show="currentStepNum === 3" class="step-content">
              <h3 class="title-h3">角色固定</h3>
              <p class="step-desc">为角色生成固定视频，确保角色形象一致性</p>
              
              <div class="character-fix-section">
                <!-- 本章人物 -->
                <div class="generation-section" v-if="extractedInfo.characters.length > 0">
                  <h4 class="section-title">本章人物</h4>
                  <div class="character-grid">
                    <div
                      v-for="character in extractedInfo.characters"
                      :key="character.id"
                      class="character-card"
                    >
                      <div class="character-avatar" v-if="character.avatar">
                        <div class="avatar-video-action-fixed" v-if="character.videoUrl">
                          <button class="avatar-action-btn text-btn" @click.stop="openVideoPreview(character.videoUrl, character.name)" title="查看视频">
                            {{ '\u67e5\u770b\u89c6\u9891' }}
                          </button>
                        </div>
                        <img
                          :src="character.avatar"
                          :alt="character.name"
                          class="character-image"
                          @click.stop="openImagePreview(character.avatar, character.name)"
                        />
                        <div class="avatar-actions">
                          <button class="avatar-action-btn" @click.stop="downloadImage(character.avatar, character.name)" title="下载图片">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                              <polyline points="7 10 12 15 17 10"/>
                              <line x1="12" y1="15" x2="12" y2="3"/>
                            </svg>
                          </button>
                        </div>
                      </div>
                      <div class="character-avatar" v-else>
                        <div class="avatar-video-action-fixed" v-if="character.videoUrl">
                          <button class="avatar-action-btn text-btn" @click.stop="openVideoPreview(character.videoUrl, character.name)" title="查看视频">
                            {{ '\u67e5\u770b\u89c6\u9891' }}
                          </button>
                        </div>
                        <div class="character-placeholder">
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                            <circle cx="12" cy="7" r="4"/>
                          </svg>
                        </div>
                      </div>
                      <div class="character-info">
                        <h5 class="character-name">{{ character.name }}</h5>
                        <p class="character-desc">{{ character.description }}</p>
                      </div>
                      <div class="character-actions">
                        <button 
                          class="btn btn-sm character-action-btn"
                          @click="generateAllCharacters"
                          :disabled="generatingCharacter.has(character.id)"
                        >
                          <svg v-if="generatingCharacter.has(character.id)" class="spinner" viewBox="0 0 24 24" width="14" height="14">
                            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                              <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                            </circle>
                          </svg>
                          <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 1l4 4-4 4"/>
                            <path d="M3 11V9a4 4 0 0 1 4-4h14"/>
                            <path d="M7 23l-4-4 4-4"/>
                            <path d="M21 13v2a4 4 0 0 1-4 4H3"/>
                          </svg>
                          固定角色
                        </button>
                        <button 
                          class="btn btn-sm character-action-btn"
                          @click="generateCharacterVideo(character)"
                          :disabled="generatingVideo.has(character.id)"
                        >
                          <svg v-if="generatingVideo.has(character.id)" class="spinner" viewBox="0 0 24 24" width="14" height="14">
                            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                              <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                            </circle>
                          </svg>
                          <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                            <polygon points="5 3 19 12 5 21 5 3"/>
                          </svg>
                          生成视频
                        </button>
                      </div>
                      <!-- 视频区域 -->
                      <div class="character-video" v-if="videoTasks[character.id] && videoTasks[character.id].status === 2 && videoTasks[character.id].videoUrl">
                      <video 
                        :src="videoTasks[character.id].videoUrl" 
                        controls
                        preload="metadata"
                        playsinline
                        class="character-video-player"
                      ></video>
                      </div>
                      <div class="character-video" v-else-if="false">
                        <div class="video-generating">
                          <svg class="spinner" viewBox="0 0 24 24" width="24" height="24">
                            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                              <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                            </circle>
                          </svg>
                          <span>{{ videoTasks[character.id]?.status === 1 ? '视频生成中...' : '等待生成视频...' }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 项目人物 -->
                <div class="generation-section" v-if="projectCharacters.length > 0">
                  <h4 class="section-title">项目人物</h4>
                  <div class="character-grid">
                    <div
                      v-for="character in projectCharacters"
                      :key="character.id"
                      class="character-card"
                    >
                      <div class="character-avatar" v-if="character.avatar">
                        <div class="avatar-video-action-fixed" v-if="character.videoUrl">
                          <button class="avatar-action-btn text-btn" @click.stop="openVideoPreview(character.videoUrl, character.name)" title="查看视频">
                            {{ '\u67e5\u770b\u89c6\u9891' }}
                          </button>
                        </div>
                        <img
                          :src="character.avatar"
                          :alt="character.name"
                          class="character-image"
                          @click.stop="openImagePreview(character.avatar, character.name)"
                        />
                      </div>
                      <div v-else class="character-avatar">
                        <div class="avatar-video-action-fixed" v-if="character.videoUrl">
                          <button class="avatar-action-btn text-btn" @click.stop="openVideoPreview(character.videoUrl, character.name)" title="查看视频">
                            {{ '\u67e5\u770b\u89c6\u9891' }}
                          </button>
                        </div>
                        <div class="character-placeholder">
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                            <circle cx="12" cy="7" r="4"/>
                          </svg>
                        </div>
                      </div>
                      <div class="character-info">
                        <h5 class="character-name">{{ character.name }}</h5>
                        <p class="character-desc">{{ character.description }}</p>
                      </div>
                      <div class="character-actions">
                        <button 
                          class="btn btn-sm character-action-btn"
                          @click="generateAllCharacters"
                          :disabled="generatingCharacter.has(character.id)"
                        >
                          <svg v-if="generatingCharacter.has(character.id)" class="spinner" viewBox="0 0 24 24" width="14" height="14">
                            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                              <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                            </circle>
                          </svg>
                          <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 1l4 4-4 4"/>
                            <path d="M3 11V9a4 4 0 0 1 4-4h14"/>
                            <path d="M7 23l-4-4 4-4"/>
                            <path d="M21 13v2a4 4 0 0 1-4 4H3"/>
                          </svg>
                          固定角色
                        </button>
                        <button 
                          class="btn btn-sm character-action-btn"
                          @click="generateCharacterVideo(character)"
                          :disabled="generatingVideo.has(character.id)"
                        >
                          <svg v-if="generatingVideo.has(character.id)" class="spinner" viewBox="0 0 24 24" width="14" height="14">
                            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                              <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                            </circle>
                          </svg>
                          <svg v-else viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                            <polygon points="5 3 19 12 5 21 5 3"/>
                          </svg>
                          生成视频
                        </button>
                      </div>
                      <!-- 视频区域 -->
                      <div class="character-video" v-if="videoTasks[character.id] && videoTasks[character.id].status === 2 && videoTasks[character.id].videoUrl">
                        <video 
                          :src="videoTasks[character.id].videoUrl" 
                          controls 
                          class="character-video-player"
                        ></video>
                      </div>
                      <div class="character-video" v-else-if="false">
                        <div class="video-generating">
                          <svg class="spinner" viewBox="0 0 24 24" width="24" height="24">
                            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                              <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                            </circle>
                          </svg>
                          <span>{{ videoTasks[character.id]?.status === 1 ? '视频生成中...' : '等待生成视频...' }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="extractedInfo.characters.length === 0 && projectCharacters.length === 0" class="empty-generation">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                  <p>暂无可固定的角色</p>
                </div>
              </div>
              
              <div class="step-actions">
                <button class="btn btn-secondary" @click="prevStep">上一步</button>
                <button class="btn btn-primary" @click="nextStep">生成分镜</button>
              </div>
            </div>
            
            <!-- 步骤4: 生成分镜 -->
            <div v-show="currentStepNum === 4" class="step-content">
              <div class="storyboard-header">
                <div>
                  <h3 class="title-h3">分镜列表</h3>
                  <p class="step-desc">
                    共 {{ storyboards.length }} 个分镜 · {{ extractedInfo.characters.length }} 个角色 · {{ extractedInfo.scenes.length }} 个场景
                  </p>
                </div>
                <div class="storyboard-actions">
                  <button class="btn btn-secondary" @click="generateStoryboards" :disabled="generatingStoryboard">
                    <svg v-if="generatingStoryboard" class="spinner" viewBox="0 0 24 24" width="14" height="14">
                      <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                        <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                      </circle>
                    </svg>
                    生成分镜
                  </button>
                </div>
              </div>

              <div class="storyboard-list" v-if="storyboards.length > 0">
                <div v-for="(sb, idx) in storyboards" :key="sb.id || idx" class="storyboard-item">
                  <button class="storyboard-delete" @click.stop="openDeleteStoryboardConfirm(sb)" title="删除分镜">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <line x1="18" y1="6" x2="6" y2="18"/>
                      <line x1="6" y1="6" x2="18" y2="18"/>
                    </svg>
                  </button>
                  <div class="storyboard-index">{{ idx + 1 }}</div>
                  <div class="storyboard-body">
                    <textarea
                      class="storyboard-text storyboard-textarea"
                      v-model="sb.description"
                      placeholder="暂无镜头描述"
                      @blur="saveStoryboardInfo(sb)"
                    ></textarea>
                    <div class="storyboard-meta">
                      <div class="meta-row clickable meta-row-characters" @click="openStoryboardCharacterPicker(sb)">
                        <span class="meta-label">关联角色</span>
                        <div class="meta-tags">
                          <span v-if="!sb.characters || sb.characters.length === 0" class="tag muted">未关联</span>
                          <span v-for="c in (sb.characters || [])" :key="c.id" class="tag">{{ c.name }}</span>
                        </div>
                      </div>
                      <div class="meta-row clickable meta-row-scenes" @click="openStoryboardScenePicker(sb)">
                        <span class="meta-label">关联场景</span>
                        <div class="meta-tags">
                          <span v-if="!sb.scenes || sb.scenes.length === 0" class="tag muted">未关联</span>
                          <span v-for="s in (sb.scenes || [])" :key="s.id" class="tag">{{ s.name }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="storyboard-empty" v-else>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                  <rect x="2" y="3" width="20" height="14" rx="2" ry="2"/>
                  <line x1="8" y1="21" x2="16" y2="21"/>
                  <line x1="12" y1="17" x2="12" y2="21"/>
                </svg>
                <span>暂无分镜，点击“生成分镜”开始生成</span>
              </div>

              <div class="step-actions">
                <button class="btn btn-secondary" @click="prevStep">上一步</button>
                <button class="btn btn-primary" @click="nextStep">生成视频</button>
              </div>
            </div>
            
            <!-- 步骤5/6: 视频生成 -->
            <div v-show="currentStepNum === 5" class="step-content video-step">
              <div class="storyboard-header">
                <div>
                  <h3 class="title-h3">制作</h3>
                  <p class="step-desc">
                    共 {{ storyboards.length }} 个分镜 · {{ extractedInfo.characters.length }} 个角色 · {{ extractedInfo.scenes.length }} 个场景
                  </p>
                </div>
                <div class="storyboard-actions">
                  <button class="btn btn-primary" @click="nextStep">视频编辑</button>
                </div>
              </div>

              <div class="video-layout">
                <aside class="video-sidebar">
                  <div class="video-panel-title">分镜列表</div>
                  <div class="shot-list">
                    <div 
                      v-for="(sb, idx) in storyboards" 
                      :key="sb.id || idx"
                      class="shot-item"
                      :class="{ active: selectedStoryboardIndex === idx }"
                      @click="selectedStoryboardIndex = idx"
                    >
                      <div class="shot-index">{{ idx + 1 }}</div>
                      <div class="shot-text">{{ sb.description || '暂无镜头描述' }}</div>
                    </div>
                    <div v-if="storyboards.length === 0" class="shot-empty">
                      暂无分镜，先生成分镜再制作视频
                    </div>
                  </div>
                </aside>

                <section class="video-preview">
                  <div class="preview-tabs">
                    <div class="tab" :class="{ active: videoPreviewTab === 'media' }" @click="videoPreviewTab = 'media'">首帧预览</div>
                    <div class="tab" :class="{ active: videoPreviewTab === 'edit' }" @click="videoPreviewTab = 'edit'">视频预览</div>
                  </div>
                  <div class="preview-canvas">
                    <video
                      v-if="selectedShotVideoUrl"
                      v-show="videoPreviewTab === 'edit'"
                      :src="selectedShotVideoUrl"
                      controls
                      preload="auto"
                      playsinline
                      class="preview-video"
                    ></video>
                    <img 
                      v-if="selectedPreviewThumb"
                      v-show="videoPreviewTab !== 'edit'"
                      :src="selectedPreviewThumb" 
                      class="preview-image" 
                      :alt="selectedStoryboard?.description || selectedScene?.name || '预览'"
                      @click="openImagePreview(selectedPreviewThumb, selectedStoryboard?.description || selectedScene?.name || '预览')"
                    />
                    <div v-if="(videoPreviewTab === 'edit' && !selectedShotVideoUrl) || (videoPreviewTab !== 'edit' && !selectedPreviewThumb)" class="preview-placeholder">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <rect x="3" y="3" width="18" height="18" rx="2"/>
                        <polygon points="9 8 16 12 9 16 9 8"/>
                      </svg>
                      <span>{{ videoPreviewTab === 'edit' ? '暂无分镜视频' : '暂无预览画面' }}</span>
                    </div>
                  </div>
                  <div class="preview-footer">
                    <div class="meta-chip">镜头类型：{{ selectedStoryboard?.shotType || '未设置' }}</div>
                    <div class="meta-chip">时长：{{ selectedStoryboard?.duration || '-' }}s</div>
                  </div>
                </section>

                <aside class="video-config">
                  <div class="video-config-tabs">
                    <button
                      class="video-config-tab"
                      :class="{ active: videoConfigTab === 'image' }"
                      @click="videoConfigTab = 'image'"
                    >
                      镜头图片
                    </button>
                    <button
                      class="video-config-tab"
                      :class="{ active: videoConfigTab === 'video' }"
                      @click="videoConfigTab = 'video'"
                    >
                      视频生成
                    </button>
                  </div>
                  <div class="video-panel-title">{{ videoConfigTab === 'image' ? '镜头配置' : '视频生成' }}</div>
                  <div class="config-section" v-show="videoConfigTab === 'image'">
                    <div class="config-label">背景图</div>
                    <div class="config-card">
                      <img v-if="selectedSceneThumb" :src="selectedSceneThumb" class="config-thumb" alt="背景图" @click="openImagePreview(selectedSceneThumb, selectedScene?.name || '场景')"/>
                      <div v-else class="config-empty">未选择</div>
                      <div class="config-actions">
                        <button class="btn btn-secondary btn-sm" @click="openScenePicker">选择</button>
                        <button class="btn btn-secondary btn-sm" @click="triggerSceneReplace">替换</button>
                        <input ref="sceneReplaceInput" type="file" accept="image/*" class="hidden-input" @change="handleSceneReplace" />
                      </div>
                    </div>
                  </div>

                  <div class="config-section" v-show="videoConfigTab === 'image'">
                    <div class="config-label">出场角色</div>
                    <div class="config-character-list">
                      <span v-if="!selectedStoryboard?.characters?.length" class="tag muted">未关联</span>
                      <div v-for="c in (selectedStoryboard?.characters || [])" :key="c.id" class="config-character-item">
                        <div class="config-character-avatar">
                          <img v-if="c.avatar" :src="c.avatar" :alt="c.name" @click="openImagePreview(c.avatar, c.name)" />
                          <span v-else>{{ (c.name || '?').charAt(0) }}</span>
                        </div>
                        <div class="config-character-name">{{ c.name }}</div>
                      </div>
                    </div>
                  </div>

                  <div class="config-section" v-show="videoConfigTab === 'image'">
                    <div class="config-label-row">
                      <div class="config-label">提示词</div>
                      <div class="config-label-actions">
                        <button class="btn btn-secondary btn-sm" :disabled="generatingFirstFrame" @click="generateFirstFramePrompt">
                          {{ generatingFirstFrame ? '生成中...' : '生成提示词' }}
                        </button>
                        <button class="btn btn-secondary btn-sm" :disabled="savingFirstFramePrompt" @click="saveFirstFramePrompt">
                          {{ savingFirstFramePrompt ? '保存中...' : '保存' }}
                        </button>
                      </div>
                    </div>
                    <textarea
                      class="config-textarea"
                      v-model="firstFramePrompt"
                      placeholder="请点击生成提示词"
                    ></textarea>
                  </div>
                  <div class="config-section" v-show="videoConfigTab === 'image'">
                    <div class="config-actions-footer">
                      <button class="btn btn-primary" :disabled="generatingFirstFrameImage" @click="generateFirstFrameImage">
                        {{ generatingFirstFrameImage ? '生成中...' : '生成图片' }}
                      </button>
                    </div>
                  </div>

                  <div class="config-section video-gen-section" v-show="videoConfigTab === 'video'">
                    <div class="video-gen-card">
                      <div class="video-gen-block">
                        <div class="video-gen-label">视频模型</div>
                        <div class="video-gen-select" tabindex="0" @blur="videoModelDropdownOpen = false">
                          <button class="video-gen-select-trigger" type="button" @click.stop="videoModelDropdownOpen = !videoModelDropdownOpen">
                            <span>{{ selectedVideoModelName || '请选择模型' }}</span>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <polyline points="6 9 12 15 18 9"/>
                            </svg>
                          </button>
                          <div v-if="videoModelDropdownOpen" class="video-gen-select-menu">
                            <button
                              v-for="m in videoModels"
                              :key="m.id"
                              class="video-gen-select-item"
                              :class="{ active: videoGenSettings.modelId === m.id }"
                              @mousedown.prevent="selectVideoModel(m)"
                            >
                              <div class="item-title">{{ m.instanceName || m.modelCode || m.modelName || m.name || '模型' }}</div>
                              <div class="item-sub">{{ m.providerCode || '模型管理' }}</div>
                            </button>
                          </div>
                        </div>
                      </div>

                      <div class="video-gen-block">
                        <div class="video-gen-label">参考图</div>
                        <div class="video-gen-ref">
                          <div class="video-gen-ref-box" @click="selectedPreviewThumb && openImagePreview(selectedPreviewThumb, '参考图')">
                            <img v-if="selectedPreviewThumb" :src="selectedPreviewThumb" alt="参考图" />
                            <div v-else class="video-gen-ref-empty">点击选择</div>
                          </div>
                        </div>
                      </div>

                      <div class="video-gen-block">
                        <div class="video-gen-label">提示词</div>
                        <textarea class="config-textarea" :value="selectedStoryboard?.videoPrompt || ''" placeholder="请输入提示词" readonly></textarea>
                      </div>

                      <div class="video-gen-block">
                        <div class="video-gen-label">时长</div>
                        <div class="video-gen-slider">
                          <input type="range" min="2" max="8" step="1" v-model="videoGenSettings.duration" />
                          <span>{{ videoGenSettings.duration }}s</span>
                        </div>
                      </div>

                      <div class="config-actions-footer">
                        <button class="btn btn-secondary" :disabled="generatingShotVideoPrompt" @click="generateShotVideoPrompt">
                          {{ generatingShotVideoPrompt ? '生成中...' : '生成提示词' }}
                        </button>
                        <button class="btn btn-primary" :disabled="!selectedPreviewThumb || generatingShotVideo.has(selectedStoryboard?.id)" @click="generateShotVideo">
                          {{ generatingShotVideo.has(selectedStoryboard?.id) ? '生成中...' : '生成视频' }}
                        </button>
                      </div>
                    </div>
                  </div>

                </aside>
              </div>

              <div class="step-actions">
                <button class="btn btn-secondary" @click="prevStep">返回编辑</button>
                <button class="btn btn-primary" @click="nextStep">视频编辑</button>
              </div>
            </div>
            
            <!-- 步骤6/7: 视频编辑 -->
            <div v-show="currentStepNum === 6" class="step-content video-edit-step">
              <div class="storyboard-header">
                <div>
                  <h3 class="title-h3">视频编辑</h3>
                  <p class="step-desc">将分镜视频拖入时间轴进行简单拼接与预览</p>
                </div>
                <div class="storyboard-actions">
                  <button class="btn btn-primary" @click="nextChapter">下一章节</button>
                </div>
              </div>

              <div class="video-editor-layout">
                <section class="editor-preview">
                  <div class="editor-tabs">
                    <div class="tab active">媒体预览</div>
                  </div>
                  <div class="editor-canvas">
                    <video
                      v-if="videoEditCurrentUrl"
                      ref="editPreviewPlayer"
                      :src="videoEditCurrentUrl"
                      controls
                      preload="auto"
                      playsinline
                      class="editor-video"
                      @ended="handleEditVideoEnded"
                    ></video>
                    <div v-else class="preview-placeholder">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <rect x="3" y="3" width="18" height="18" rx="2"/>
                        <polygon points="9 8 16 12 9 16 9 8"/>
                      </svg>
                      <span>将分镜拖到时间轴开始编辑</span>
                    </div>
                  </div>
                  <div class="editor-controls">
                    <div class="editor-duration">总时长：{{ videoEditTotalDuration }}s</div>
                    <button class="btn btn-primary btn-sm" @click="startVideoConcat">合成预览</button>
                  </div>
                </section>

                <aside class="editor-clips">
                  <div class="editor-tabs">
                    <div class="tab active">视频 ({{ availableVideoClips.length }})</div>
                    <div class="tab">音频 (0)</div>
                  </div>
                  <button class="btn btn-secondary btn-sm add-all-btn" @click="addAllVideoClips">添加全部</button>
                  <div class="clip-list">
                    <div
                      v-for="clip in availableVideoClips"
                      :key="clip.id"
                      class="clip-card"
                      @click="addVideoClip(clip)"
                    >
                      <div class="clip-thumb">
                        <img :src="clip.thumb" :alt="clip.title" />
                        <span class="clip-duration">{{ clip.duration }}s</span>
                      </div>
                      <div class="clip-title">{{ clip.title }}</div>
                    </div>
                    <div v-if="availableVideoClips.length === 0" class="clip-empty">暂无可用分镜视频</div>
                  </div>
                </aside>
              </div>

              <div class="editor-timeline">
                <div class="timeline-header">
                  <span>视频轨道</span>
                  <span class="timeline-count">片段 {{ videoEditClips.length }} 个</span>
                </div>
                <div class="timeline-track">
                  <div v-if="videoEditClips.length === 0" class="timeline-empty">暂无片段，请从右侧添加</div>
                  <div v-else class="timeline-clips">
                    <div
                      v-for="(clip, idx) in videoEditClips"
                      :key="clip.id + '-' + idx"
                      class="timeline-clip"
                    >
                      <span class="clip-index">{{ idx + 1 }}</span>
                      <span class="clip-name">{{ clip.title }}</span>
                      <span class="clip-length">{{ clip.duration }}s</span>
                      <div class="clip-actions">
                        <button class="icon-btn" @click="moveClip(idx, -1)" :disabled="idx === 0">←</button>
                        <button class="icon-btn" @click="moveClip(idx, 1)" :disabled="idx === videoEditClips.length - 1">→</button>
                        <button class="icon-btn danger" @click="removeClip(idx)">×</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="step-actions">
                <button class="btn btn-secondary" @click="prevStep">返回制作</button>
              </div>
            </div>
          </template>
        </div>
      </main>
      
      <!-- 右侧：信息面板 -->
      <aside class="info-panel" :class="{ collapsed: rightPanelCollapsed }">
        <div class="panel-tabs" v-show="!rightPanelCollapsed">
          <button 
            class="panel-tab"
            :class="{ active: rightPanelTab === 'model' }"
            @click="rightPanelTab = 'model'"
          >
            模型选择
          </button>
          <button 
            class="panel-tab"
            :class="{ active: rightPanelTab === 'assets' }"
            @click="rightPanelTab = 'assets'"
          >
            项目资源
          </button>
        </div>
        
        <!-- 模型选择面板 -->
        <div class="panel-content" v-show="rightPanelTab === 'model'">
          <div class="panel-section">
            <h4>{{ currentModelTypeName }}</h4>
            <div class="model-list">
              <div 
                v-for="model in currentModels" 
                :key="model.id"
                class="model-item"
                :class="{ selected: selectedModel === model.id, 'is-default': isDefaultModel(model) }"
                @click="selectedModel = model.id"
              >
                <div class="model-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
                  </svg>
                </div>
                <div class="model-info">
                  <span class="model-name">{{ model.instanceName || model.modelCode }}</span>
                  <span class="model-provider">{{ model.providerCode || '未知提供商' }}</span>
                </div>
                <span class="model-badge" v-if="isDefaultModel(model)">默认</span>
              </div>
              <div class="empty-models" v-if="currentModels.length === 0">
                <p>暂无可用模型</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 项目资源面板 -->
        <div class="panel-content" v-show="rightPanelTab === 'assets'">
          <div class="panel-section">
            <h4>项目人物 ({{ projectCharacters.length }})</h4>
            <div class="project-assets-list">
              <div 
                v-for="char in projectCharacters" 
                :key="char.id"
                class="asset-item"
              >
                <div class="asset-avatar">
                  <img :src="char.avatar || defaultAvatar" :alt="char.name" />
                </div>
                <div class="asset-info">
                  <span class="asset-name">{{ char.name }}</span>
                  <span class="asset-episode">来自: {{ getEpisodeName(char.episodeId) }}</span>
                </div>
              </div>
              <div class="empty-assets" v-if="projectCharacters.length === 0">
                <p>暂无项目人物</p>
              </div>
            </div>
          </div>
          
          <div class="panel-section">
            <h4>项目场景 ({{ projectScenes.length }})</h4>
            <div class="project-assets-list">
              <div 
                v-for="scene in projectScenes" 
                :key="scene.id"
                class="asset-item"
              >
                <div class="asset-avatar scene-avatar">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="3" width="18" height="18" rx="2"/>
                  </svg>
                </div>
                <div class="asset-info">
                  <span class="asset-name">{{ scene.name }}</span>
                  <span class="asset-episode">来自: {{ getEpisodeName(scene.episodeId) }}</span>
                </div>
              </div>
              <div class="empty-assets" v-if="projectScenes.length === 0">
                <p>暂无项目场景</p>
              </div>
            </div>
          </div>
        </div>
      </aside>

      <button
        class="collapse-edge-btn left"
        :style="{ left: leftPanelCollapsed ? '0px' : '220px' }"
        @click="leftPanelCollapsed = !leftPanelCollapsed"
        :title="leftPanelCollapsed ? '展开章节列表' : '折叠章节列表'"
      >
        <span v-if="!leftPanelCollapsed">◀</span>
        <span v-else>▶</span>
      </button>

      <button
        class="collapse-edge-btn right"
        :style="{ right: rightPanelCollapsed ? '0px' : '260px' }"
        @click="rightPanelCollapsed = !rightPanelCollapsed"
        :title="rightPanelCollapsed ? '展开侧边栏' : '折叠侧边栏'"
      >
        <span v-if="!rightPanelCollapsed">▶</span>
        <span v-else>◀</span>
      </button>
    </div>

    <Teleport to="body">
      <transition name="modal">
        <div v-if="showScenePicker" class="modal-overlay" @click.self="showScenePicker = false">
          <div class="modal prompt-modal scene-picker-modal">
            <div class="modal-header">
              <h3>选择背景场景</h3>
              <button class="close-btn" @click="showScenePicker = false">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="scene-picker-grid">
                <button
                  v-for="scene in projectScenes"
                  :key="scene.id"
                  class="scene-picker-item"
                  @click="selectStoryboardScene(scene)"
                >
                  <div class="scene-picker-thumb">
                    <img :src="getSceneThumbnail(scene)" :alt="scene.name" />
                  </div>
                  <div class="scene-picker-name">{{ scene.name }}</div>
                </button>
              </div>
              <div v-if="!projectScenes.length" class="scene-picker-empty">暂无项目场景</div>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 创建章节弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showChapterModal" class="modal-overlay" @click.self="showChapterModal = false">
          <div class="modal">
            <div class="modal-header">
              <h3 class="title-h3">创建章节</h3>
              <button class="close-btn" @click="showChapterModal = false">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="form-label">章节序号</label>
                <input type="number" class="input" v-model="newEpisode.chapterNumber" placeholder="请输入章节序号" />
              </div>
              <div class="form-group">
                <label class="form-label">章节名称</label>
                <input type="text" class="input" v-model="newEpisode.name" placeholder="请输入章节名称" />
              </div>
              <div class="form-group">
                <label class="form-label">章节内容</label>
                <textarea class="input textarea" v-model="newEpisode.content" placeholder="请输入章节内容（可选）"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="showChapterModal = false">取消</button>
              <button class="btn btn-primary" @click="handleAddChapter" :disabled="!canCreateChapter">
                创建章节
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 错误提示弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showErrorModal" class="modal-overlay" @click.self="showErrorModal = false">
          <div class="modal result-modal">
            <div class="result-content">
              <div class="result-icon error">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <line x1="15" y1="9" x2="9" y2="15"/>
                  <line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
              </div>
              <div class="result-text">
                <h3>操作失败</h3>
                <p>{{ errorMessage }}</p>
              </div>
            </div>
            <button class="btn btn-primary btn-block" @click="showErrorModal = false">确定</button>
          </div>
        </div>
      </transition>

      <!-- 结果提示弹窗 -->
      <transition name="modal">
        <div v-if="showResult" class="modal-overlay">
          <div class="modal result-modal">
            <div class="result-content">
              <div class="result-icon" :class="result.success ? 'success' : 'error'">
                <svg v-if="result.success" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </div>
              <div class="result-text">
                <h3>{{ result.success ? '操作成功' : '操作失败' }}</h3>
                <p>{{ result.message }}</p>
              </div>
            </div>
            <button class="btn btn-primary btn-block" @click="showResult = false">确定</button>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 更新章节弹窗 -->
    <Teleport to="body">
      <transition name="modal">
        <div v-if="showUpdateEpisodeModal" class="modal-overlay" @click.self="showUpdateEpisodeModal = false">
          <div class="modal">
            <div class="modal-header">
              <h3 class="title-h3">更新章节信息</h3>
              <button class="close-btn" @click="showUpdateEpisodeModal = false">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-group">
                <label class="form-label">章节序号</label>
                <input type="number" class="input" v-model.number="updateEpisodeForm.chapterNumber" placeholder="请输入章节序号" />
              </div>
              <div class="form-group">
                <label class="form-label">章节名称</label>
                <input type="text" class="input" v-model="updateEpisodeForm.name" placeholder="请输入章节名称" />
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="showUpdateEpisodeModal = false">取消</button>
              <button class="btn btn-primary" @click="handleUpdateEpisodeMeta" :disabled="!canUpdateEpisodeMeta || updatingEpisodeMeta">
                <svg v-if="updatingEpisodeMeta" class="spinner" viewBox="0 0 24 24" width="16" height="16">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                    <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
                  </circle>
                </svg>
                更新章节
              </button>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <!-- 删除章节确认弹窗 -->
    <div class="modal-overlay" v-if="deleteEpisodeConfirm.visible">
      <div class="modal">
        <div class="modal-header">
          <h4>确认删除</h4>
          <button class="modal-close" @click="closeDeleteEpisodeConfirm" aria-label="关闭">×</button>
        </div>
        <div class="modal-body">
          <p>确定要删除该章节吗？</p>
          <p class="modal-tip">删除后该章节内容和相关任务将被清除。</p>
          <p class="form-error" v-if="deleteEpisodeConfirm.error">{{ deleteEpisodeConfirm.error }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeDeleteEpisodeConfirm">取消</button>
          <button class="btn btn-primary" :disabled="deleteEpisodeConfirm.loading" @click="confirmDeleteEpisode">
            <svg v-if="deleteEpisodeConfirm.loading" class="spinner" viewBox="0 0 24 24" width="16" height="16">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
              </circle>
            </svg>
            确认删除
          </button>
        </div>
      </div>
    </div>

    <!-- 删除分镜确认弹窗 -->
    <div class="modal-overlay" v-if="deleteStoryboardConfirm.visible">
      <div class="modal">
        <div class="modal-header">
          <h4>确认删除</h4>
          <button class="modal-close" @click="closeDeleteStoryboardConfirm" aria-label="关闭">×</button>
        </div>
        <div class="modal-body">
          <p>确定要删除该分镜吗？</p>
          <p class="modal-tip">删除后相关生成任务将被清除。</p>
          <p class="form-error" v-if="deleteStoryboardConfirm.error">{{ deleteStoryboardConfirm.error }}</p>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeDeleteStoryboardConfirm">取消</button>
          <button class="btn btn-primary" :disabled="deleteStoryboardConfirm.loading" @click="deleteStoryboard">
            <svg v-if="deleteStoryboardConfirm.loading" class="spinner" viewBox="0 0 24 24" width="16" height="16">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" fill="none" stroke-dasharray="31.416" stroke-dashoffset="31.416">
                <animate attributeName="stroke-dashoffset" values="31.416;0" dur="1s" repeatCount="indefinite"/>
              </circle>
            </svg>
            确认删除
          </button>
        </div>
      </div>
    </div>

    <!-- 选择分镜角色 -->
    <div class="modal-overlay" v-if="storyboardCharacterPicker.visible">
      <div class="modal character-picker-modal">
        <div class="modal-header">
          <h4>选择角色</h4>
          <button class="modal-close" @click="closeStoryboardCharacterPicker" aria-label="关闭">×</button>
        </div>
        <div class="modal-body">
          <div class="character-picker-search" @click.stop>
            <svg class="character-picker-search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.35-4.35" />
            </svg>
            <input
              v-model.trim="storyboardCharacterSearchKeyword"
              class="character-picker-search-input"
              type="text"
              placeholder="搜索角色名称/描述"
              @input="handleStoryboardCharacterSearchInput"
              @focus="handleStoryboardCharacterSearchFocus"
            />
            <button
              v-if="storyboardCharacterSearchKeyword"
              class="character-picker-search-clear"
              @click="clearStoryboardCharacterSearch"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18" />
                <line x1="6" y1="6" x2="18" y2="18" />
              </svg>
            </button>
            <div v-if="showStoryboardCharacterSuggestDropdown" class="character-picker-suggest-dropdown">
              <button
                v-for="item in storyboardCharacterSuggestions"
                :key="item.id || item.name || item"
                class="character-picker-suggest-item"
                @click="applyStoryboardCharacterSuggestion(item)"
              >
                <img
                  v-if="item.avatar"
                  :src="item.avatar"
                  :alt="item.name || '角色'"
                  class="character-picker-suggest-avatar"
                />
                <div class="character-picker-suggest-text">
                  <span class="character-picker-suggest-name">{{ item.name || item }}</span>
                </div>
              </button>
            </div>
          </div>
          <div class="character-picker-grid">
            <button
              v-for="ch in storyboardCharacterDisplayList"
              :key="ch.id"
              class="character-card"
              :class="{ selected: storyboardCharacterPicker.selected.has(String(ch.id)) }"
              @click="toggleStoryboardCharacter(ch)"
            >
              <div class="card-avatar">
                <img v-if="ch.avatar" :src="ch.avatar" :alt="ch.name" />
                <div v-else class="card-avatar-placeholder">{{ (ch.name || '?').charAt(0) }}</div>
              </div>
              <div class="card-info">
                <div class="card-name">{{ ch.name }}</div>
                <div class="card-desc">{{ ch.description || '暂无描述' }}</div>
              </div>
            </button>
          </div>
          <div v-if="!storyboardCharacterDisplayList.length" class="character-picker-empty">未找到匹配角色</div>
        </div>
        <div class="modal-footer">
          <span class="select-limit">最多选择 3 个角色</span>
          <div class="footer-actions">
            <button class="btn btn-secondary" @click="closeStoryboardCharacterPicker">取消</button>
            <button class="btn btn-primary" @click="confirmStoryboardCharacters">确认</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 选择分镜场景 -->
    <div class="modal-overlay" v-if="storyboardScenePicker.visible">
      <div class="modal storyboard-scene-picker-modal">
        <div class="modal-header">
          <h4>选择场景</h4>
          <button class="modal-close" @click="closeStoryboardScenePicker" aria-label="关闭">×</button>
        </div>
        <div class="modal-body">
          <div class="character-picker-search" @click.stop>
            <svg class="character-picker-search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.35-4.35" />
            </svg>
            <input
              v-model.trim="storyboardSceneSearchKeyword"
              class="character-picker-search-input"
              type="text"
              placeholder="搜索场景名称/描述"
              @input="handleStoryboardSceneSearchInput"
              @focus="handleStoryboardSceneSearchFocus"
            />
            <button
              v-if="storyboardSceneSearchKeyword"
              class="character-picker-search-clear"
              @click="clearStoryboardSceneSearch"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18" />
                <line x1="6" y1="6" x2="18" y2="18" />
              </svg>
            </button>
            <div v-if="showStoryboardSceneSuggestDropdown" class="character-picker-suggest-dropdown">
              <button
                v-for="item in storyboardSceneSuggestions"
                :key="item.id || item.name || item"
                class="character-picker-suggest-item"
                @click="applyStoryboardSceneSuggestion(item)"
              >
                <img
                  v-if="item.thumbnail"
                  :src="item.thumbnail"
                  :alt="item.name || '场景'"
                  class="character-picker-suggest-avatar"
                />
                <div class="character-picker-suggest-text">
                  <span class="character-picker-suggest-name">{{ item.name || item }}</span>
                </div>
              </button>
            </div>
          </div>
          <div class="scene-select-grid">
            <button
              v-for="sc in storyboardSceneDisplayList"
              :key="sc.id"
              class="scene-select-card"
              :class="{ selected: storyboardScenePicker.selected.has(String(sc.id)) }"
              @click="toggleStoryboardScene(sc)"
            >
              <div class="scene-select-thumb">
                <img :src="getSceneThumbnail(sc)" :alt="sc.name" />
              </div>
              <div class="card-info">
                <div class="card-name">{{ sc.name }}</div>
                <div class="card-desc">{{ sc.description || '暂无描述' }}</div>
              </div>
            </button>
          </div>
          <div v-if="!storyboardSceneDisplayList.length" class="scene-picker-empty">暂无可选场景</div>
        </div>
        <div class="modal-footer">
          <span class="select-limit">最多选择 1 个场景</span>
          <div class="footer-actions">
            <button class="btn btn-secondary" @click="closeStoryboardScenePicker">取消</button>
            <button class="btn btn-primary" @click="confirmStoryboardScenes">确认</button>
          </div>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <!-- 图片预览弹窗 -->
      <transition name="modal">
        <div v-if="imagePreview" class="image-preview-overlay" @click="closeImagePreview">
          <div class="image-preview-header">
            <button class="preview-download-btn" @click="downloadCurrentImage" title="下载图片">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="7 10 12 15 17 10"/>
                <line x1="12" y1="15" x2="12" y2="3"/>
              </svg>
            </button>
            <button class="preview-close-btn" @click="closeImagePreview" title="关闭">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
          <div class="image-preview-container">
            <img :src="previewImage.url" :alt="previewImage.name" @click.stop />
          </div>
          <div class="image-preview-name">{{ previewImage.name }}</div>
        </div>
      </transition>
      <transition name="modal">
        <div v-if="videoPreview" class="image-preview-overlay" @click="closeVideoPreview">
          <div class="image-preview-header">
            <button class="preview-close-btn" @click="closeVideoPreview" title="关闭预览">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
          <div class="image-preview-container" @click.stop>
            <video :src="previewVideo.url" controls preload="auto" playsinline class="video-preview-player"></video>
          </div>
          <div class="image-preview-name">{{ previewVideo.name }}</div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { projectApi, episodeApi, modelInstanceApi, characterApi, sceneApi, storyboardApi } from '@/api'
import { useGlobalStore } from '@/stores/global'
import { useToast } from '@/composables/useToast'

const route = useRoute()
const globalStore = useGlobalStore()
const toast = useToast()

const projectId = route.params.id
const projectName = ref('')
const consistency = ref(0)
const currentStep = ref(0)
const maxReachedStep = ref(0)
const activeEpisode = ref(null)
const currentEpisodeName = ref('')
const overallProgress = ref(0)
const currentContent = ref('')
const lastSavedContent = ref('')
const saveStatus = ref('saved')
const extracting = ref(false)
const imageTab = ref('人物')
const newEpisode = ref({
  chapterNumber: 1,
  name: '',
  content: ''
})
const showChapterModal = ref(false)
const showUpdateEpisodeModal = ref(false)
const updatingEpisodeMeta = ref(false)
const updateEpisodeForm = ref({
  chapterNumber: null,
  name: ''
})
const projectCharacters = ref([])
const projectScenes = ref([])
const imageTabs = ['人物', '场景']
const generatingCharacter = ref(new Set())
const selectedCharacterIds = ref(new Set())
const generatingVideo = ref(new Set())
const videoTasks = ref({}) // characterId -> VideoTask
const videoPollingIntervals = ref({}) // characterId -> intervalId
const wsConnection = ref(null)
const wsReconnectTimer = ref(null)
const wsRetryCount = ref(0)
const wsManualClose = ref(false)
const rightPanelTab = ref('model')
const rightPanelCollapsed = ref(false)
const leftPanelCollapsed = ref(false)
const showResult = ref(false)
const result = ref({ success: false, message: '' })
const showErrorModal = ref(false)
const errorMessage = ref('')
const imagePreview = ref(false)
const previewImage = ref({ url: '', name: '' })
const videoPreview = ref(false)
const previewVideo = ref({ url: '', name: '' })
const textModels = ref([])
const imageModels = ref([])
const videoModels = ref([])
const selectedModel = ref(null)
const defaultTextModelId = ref(null)
const defaultImageModelId = ref(null)
const defaultVideoModelId = ref(null)
const saveCharacterTimers = new Map()
const addingCharacter = ref(false)
const showAddCharacterDrawer = ref(false)
const addCharacterForm = ref({
  name: '',
  description: ''
})
const addCharacterError = ref('')
const loadingProjectAssets = ref(false)
const deletingCharacters = ref(new Set())
const uploadingCharacters = ref(new Set())
const draggingCharacters = ref(new Set())
const deleteConfirm = ref({
  visible: false,
  character: null,
  error: '',
  loading: false
})
const generatingScene = ref(new Set())
const saveSceneTimers = new Map()
const deletingScenes = ref(new Set())
const deleteSceneConfirm = ref({
  visible: false,
  scene: null,
  error: '',
  loading: false
})
const deleteEpisodeConfirm = ref({
  visible: false,
  episodeId: null,
  error: '',
  loading: false
})
const deleteStoryboardConfirm = ref({
  visible: false,
  storyboard: null,
  error: '',
  loading: false
})
const storyboardCharacterPicker = ref({
  visible: false,
  storyboardId: null,
  list: [],
  selected: new Set()
})
const storyboardCharacterSearchKeyword = ref('')
const storyboardCharacterBaseList = ref([])
const storyboardCharacterSuggestions = ref([])
const storyboardCharacterFullMap = ref(new Map())
const showStoryboardCharacterSuggestDropdown = ref(false)
let storyboardCharacterSearchTimer = null
let storyboardCharacterSuggestTimer = null
const storyboardScenePicker = ref({
  visible: false,
  storyboardId: null,
  list: [],
  selected: new Set()
})
const storyboardSceneSearchKeyword = ref('')
const storyboardSceneBaseList = ref([])
const storyboardSceneSuggestions = ref([])
const storyboardSceneFullMap = ref(new Map())
const showStoryboardSceneSuggestDropdown = ref(false)
let storyboardSceneSearchTimer = null
let storyboardSceneSuggestTimer = null
const uploadingScene = ref(new Set())
const draggingScenes = ref(new Set())
const showAddSceneDrawer = ref(false)
const addSceneForm = ref({
  name: '',
  description: ''
})
const addSceneError = ref('')
const addingScene = ref(false)
const generatingFirstFrame = ref(false)
const generatingFirstFrameImage = ref(false)
const generatingShotVideoPrompt = ref(false)
const generatingShotVideo = ref(new Set())
const shotVideoTasks = ref({})
const shotVideoPollingIntervals = ref({})
const showScenePicker = ref(false)
const sceneReplaceInput = ref(null)
const videoConfigTab = ref('image')
const videoGenSettings = ref({
  modelId: null,
  refType: 'image',
  resolution: '480p',
  aspect: '16:9',
  duration: 4
})
const videoModelDropdownOpen = ref(false)
const firstFramePrompt = ref('')
const savingFirstFramePrompt = ref(false)

const getSceneThumbnail = (scene) => {
  if (scene?.thumbnail) return scene.thumbnail
  const txt = (scene?.name || '场景').charAt(0)
  return 'data:image/svg+xml,' + encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="225" viewBox="0 0 400 225">
      <defs><linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" style="stop-color:#8B7355"/><stop offset="100%" style="stop-color:#5b4630"/>
      </linearGradient></defs>
      <rect fill="url(#bg)" width="400" height="225"/>
      <text fill="#F4E4BA" font-family="Arial" font-size="64" font-weight="700" x="50%" y="55%" text-anchor="middle" dominant-baseline="middle">${txt}</text>
    </svg>`
  )
}

const hasRealAvatar = (character) => {
  const avatar = character?.avatar
  if (!avatar) return false
  return !String(avatar).startsWith('data:image/svg+xml')
}

const isCharacterSelected = (characterId) => selectedCharacterIds.value.has(characterId)

const toggleCharacterSelection = (characterId) => {
  const next = new Set(selectedCharacterIds.value)
  if (next.has(characterId)) {
    next.delete(characterId)
  } else {
    next.add(characterId)
  }
  selectedCharacterIds.value = next
}

const clearCharacterSelection = () => {
  selectedCharacterIds.value = new Set()
}

const mapExtractedInfo = (data) => ({
  characters: (data?.characters || []).map(c => ({
    id: c.id,
    name: c.name,
    description: c.description,
    avatar: c.avatar || 'data:image/svg+xml,' + encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><rect fill="#D4AF37" width="100" height="100"/><text fill="#fff" x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" font-size="40">${c.name?.charAt(0) || '?'}</text></svg>`),
    videoUrl: c.videoUrl || ''
  })),
  scenes: (data?.scenes || []).map(s => ({
    id: s.id,
    name: s.name,
    description: s.description || '',
    thumbnail: s.thumbnail || ''
  }))
})

// 当前步骤是否为生成图片步骤（步骤2）
const isImageStep = computed(() => currentStepNum.value === 2)

// 当前步骤是否为角色固定视频步骤（步骤3）
const isVideoStep = computed(() => currentStepNum.value === 3)

// 当前应该显示的模型列表
const currentModels = computed(() => {
  if (isVideoStep.value) {
    return videoModels.value
  }
  return isImageStep.value ? imageModels.value : textModels.value
})

// 当前模型类型名称
const isDefaultModel = (model) => {
  if (!model) return false
  if (isVideoStep.value) {
    return model.id === defaultVideoModelId.value
  }
  if (isImageStep.value) {
    return model.id === defaultImageModelId.value
  }
  return model.id === defaultTextModelId.value
}

const currentModelTypeName = computed(() => {
  if (isVideoStep.value) {
    return '视频模型'
  }
  return isImageStep.value ? '图像模型' : '文本模型'
})

const statusText = { 0: '待开始', 1: '处理中', 2: '已完成' }
const statusClass = { 0: 'pending', 1: 'processing', 2: 'completed' }

const workflowSteps = [
  { id: 'input', name: '输入内容' },
  { id: 'extract', name: '提取信息' },
  { id: 'images', name: '生成图片' },
  { id: 'character', name: '角色固定' },
  { id: 'storyboard', name: '生成分镜' },
  { id: 'video', name: '生成视频' },
  { id: 'edit', name: '视频编辑' }
]

const saveStatusText = computed(() => {
  const texts = { saved: '已保存', saving: '保存中...', unsaved: '未保存' }
  return texts[saveStatus.value] || texts.saved
})

const currentStepNum = computed(() => Number(currentStep.value) || 0)

const defaultAvatar = 'data:image/svg+xml,' + encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><circle fill="#D4AF37" cx="24" cy="24" r="24"/><text fill="#fff" x="24" y="28" text-anchor="middle" font-size="16">?</text></svg>`)

const episodes = ref([])

const extractedInfo = ref({
  characters: [],
  scenes: []
})

const selectedCharacterCount = computed(() =>
  extractedInfo.value.characters.filter(c => selectedCharacterIds.value.has(c.id)).length
)

const hasExtractedInfo = computed(() => {
  return extractedInfo.value.characters.length > 0 || extractedInfo.value.scenes.length > 0
})

const addChapter = () => {
  newEpisode.value = {
    chapterNumber: episodes.value.length + 1,
    name: '',
    content: ''
  }
  showChapterModal.value = true
}

const canCreateChapter = computed(() => {
  const chapterNumber = Number(newEpisode.value.chapterNumber)
  const chapterName = String(newEpisode.value.name || '').trim()
  return Number.isFinite(chapterNumber) && chapterNumber > 0 && chapterName.length > 0
})

const canUpdateEpisodeMeta = computed(() => {
  const chapterNumber = Number(updateEpisodeForm.value.chapterNumber)
  const chapterName = String(updateEpisodeForm.value.name || '').trim()
  return Number.isFinite(chapterNumber) && chapterNumber > 0 && chapterName.length > 0
})

const openUpdateEpisodeModal = () => {
  if (!activeEpisode.value) return
  const episodeMeta = episodes.value.find(e => e.id === activeEpisode.value) || {}
  updateEpisodeForm.value = {
    chapterNumber: episodeMeta.chapterNumber ?? null,
    name: episodeMeta.name || currentEpisodeName.value || ''
  }
  showUpdateEpisodeModal.value = true
}

const handleAddChapter = async () => {
  if (!canCreateChapter.value) return
  try {
    const result = await episodeApi.create(
      parseInt(projectId),
      newEpisode.value.chapterNumber,
      String(newEpisode.value.name || '').trim(),
      newEpisode.value.content ?? ''
    )
    if (result.code === 200 && result.data) {
      episodes.value.push(result.data)
      selectEpisode(result.data.id, true) // 新创建的章节强制设置为步骤 0
      showChapterModal.value = false
    }
  } catch (err) {
    console.error('创建章节失败:', err)
  }
}

const handleUpdateEpisodeMeta = async () => {
  if (!activeEpisode.value || !canUpdateEpisodeMeta.value || updatingEpisodeMeta.value) return
  updatingEpisodeMeta.value = true
  try {
    await episodeApi.update({
      id: activeEpisode.value,
      projectId: Number(projectId),
      chapterNumber: Number(updateEpisodeForm.value.chapterNumber),
      name: String(updateEpisodeForm.value.name || '').trim(),
      content: currentContent.value ?? ''
    })
    const idx = episodes.value.findIndex(e => e.id === activeEpisode.value)
    if (idx !== -1) {
      episodes.value[idx].chapterNumber = Number(updateEpisodeForm.value.chapterNumber)
      episodes.value[idx].name = String(updateEpisodeForm.value.name || '').trim()
    }
    currentEpisodeName.value = String(updateEpisodeForm.value.name || '').trim()
    showUpdateEpisodeModal.value = false
    result.value = { success: true, message: '章节信息已更新' }
    showResult.value = true
  } catch (err) {
    result.value = { success: false, message: err.message || '更新章节失败' }
    showResult.value = true
  } finally {
    updatingEpisodeMeta.value = false
  }
}

const handleDeleteEpisode = (episodeId) => {
  deleteEpisodeConfirm.value.visible = true
  deleteEpisodeConfirm.value.episodeId = episodeId
  deleteEpisodeConfirm.value.error = ''
}

const closeDeleteEpisodeConfirm = () => {
  if (deleteEpisodeConfirm.value.loading) return
  deleteEpisodeConfirm.value.visible = false
  deleteEpisodeConfirm.value.episodeId = null
  deleteEpisodeConfirm.value.error = ''
}

const confirmDeleteEpisode = async () => {
  const episodeId = deleteEpisodeConfirm.value.episodeId
  if (!episodeId || deleteEpisodeConfirm.value.loading) return

  deleteEpisodeConfirm.value.loading = true
  deleteEpisodeConfirm.value.error = ''
  try {
    await episodeApi.delete(episodeId)
    const index = episodes.value.findIndex(e => e.id === episodeId)
    if (index > -1) {
      episodes.value.splice(index, 1)
    }

    if (activeEpisode.value === episodeId) {
      if (episodes.value.length > 0) {
        await selectEpisode(episodes.value[0].id)
      } else {
        activeEpisode.value = null
        currentContent.value = ''
        extractedInfo.value = { characters: [], scenes: [] }
      }
    }
    deleteEpisodeConfirm.value.visible = false
    deleteEpisodeConfirm.value.episodeId = null
    deleteEpisodeConfirm.value.error = ''
  } catch (err) {
    console.error('删除章节失败:', err)
    deleteEpisodeConfirm.value.error = err.message || '删除失败'
  } finally {
    deleteEpisodeConfirm.value.loading = false
  }
}

const selectEpisode = async (episodeId, forceStepZero = false) => {
  if (lastSavedContent.value !== currentContent.value && saveStatus.value === 'unsaved') {
    await saveEpisode()
  }
  activeEpisode.value = episodeId
  clearCharacterSelection()
  generatingCharacter.value.clear()
  generatingScene.value.clear()
  // 如果是强制设置为步骤0（比如刚创建的章节），否则保持原有步骤
  if (forceStepZero) {
    currentStep.value = 0
  }
  currentContent.value = ''
  overallProgress.value = 0
  saveStatus.value = 'saved'

  // 加载模型数据（确保右侧面板有数据）
  await loadTextModels()

  await loadEpisodeContent(episodeId, forceStepZero)
}

const loadEpisodeContent = async (episodeId, forceStepZero = false) => {
  try {
    console.log('寮€始加载章节内容，ID:', episodeId) // 调试日志
    const result = await episodeApi.get(episodeId)
    console.log('API 返回结果:', result) // 调试日志

    if (result.code === 200 && result.data) {
      const episode = result.data
      console.log('加载章节数据:', episode) // 调试日志
      currentEpisodeName.value = episode.name || '未命名章节'
      currentContent.value = episode.content || ''
      console.log('设置内容:', episode.content) // 调试日志

      // 处理 currentStep，支持数字和枚举对象格式
      if (forceStepZero) {
        // 新创建的章节强制设置为步骤 0
        currentStep.value = 0
        console.log('强制设置步骤为 0（新创建章节）')
      } else {
        // 正常加载时使用后端返回的步骤
        if (typeof episode.currentStep === 'number') {
          currentStep.value = episode.currentStep
        } else if (typeof episode.currentStep === 'string' && !Number.isNaN(Number(episode.currentStep))) {
          currentStep.value = Number(episode.currentStep)
        } else if (typeof episode.currentStep === 'string') {
          const stepKeyMap = {
            INPUT_CONTENT: 0,
            EXTRACT_INFO: 1,
            GENERATE_IMAGES: 2,
            CHARACTER_FIX: 3,
            GENERATE_STORYBOARD: 4,
            GENERATE_VIDEO: 5,
            COMPLETED: 6
          }
          currentStep.value = stepKeyMap[episode.currentStep] ?? 0
        } else if (episode.currentStep && typeof episode.currentStep === 'object' && episode.currentStep.code !== undefined) {
          currentStep.value = episode.currentStep.code
        } else {
          currentStep.value = 0
        }
      }
      maxReachedStep.value = Math.max(0, Number(currentStep.value) || 0)
      overallProgress.value = episode.progress || 0
      lastSavedContent.value = episode.content || ''
      console.log('设置步骤:', currentStep.value) // 调试日志
    } else {
      console.warn('API 返回数据无效:', result)
      // 设置默认值
      currentEpisodeName.value = '未命名章节'
      currentContent.value = ''
      currentStep.value = 0
      maxReachedStep.value = 0
      overallProgress.value = 0
      lastSavedContent.value = ''
    }

    await loadExtractedInfo(episodeId)
  } catch (err) {
    console.error('获取章节内容失败:', err)
    // 设置默认值以防完全失败
    currentEpisodeName.value = '加载失败'
    currentContent.value = ''
    currentStep.value = 0
    overallProgress.value = 0
    lastSavedContent.value = ''
  }
}

const loadExtractedInfo = async (episodeId) => {
  try {
    const propertyResult = await episodeApi.getProperty(episodeId)
    if (propertyResult.code === 200 && propertyResult.data) {
      extractedInfo.value = {
        characters: (propertyResult.data.characters || []).map(c => ({
          id: c.id,
          name: c.name,
          description: c.description,
          avatar: c.avatar || 'data:image/svg+xml,' + encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><rect fill="#D4AF37" width="100" height="100"/><text fill="#fff" x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" font-size="40">${c.name?.charAt(0) || '?'}</text></svg>`)
        })),
        scenes: (propertyResult.data.scenes || []).map(s => ({
          id: s.id,
          name: s.name,
          description: s.description || '',
          thumbnail: s.thumbnail || ''
        }))
      }
      clearCharacterSelection()
    }
  } catch (err) {
    console.log('暂无提取信息或获取失败')
  }
}

const generateCharacterImage = async (character, actionType) => {
  if (!character || !character.id) return

  if (generatingCharacter.value.has(character.id)) return
  generatingCharacter.value.add(character.id)

  try {
    const defaultRes = await modelInstanceApi.getDefault('IMAGE')
    const defaultModel = defaultRes?.data

    if (!defaultModel) {
      toast.error('未找到默认图像生成模型，请先配置模型')
      return
    }

    const requestData = {
      modelInstanceId: defaultModel.id,
      characterId: character.id,
      projectId: projectId,
      description: character.description || `${character.name}的形象描述`
    }

    const response = await episodeApi.generateCharacterImage(requestData)
    console.log('生成角色图片响应:', response)

    if (response.code === 200 && response.data) {
      // 更新角色头像
      character.avatar = response.data.avatar

      // 如果是本章人物，更新extractedInfo
      const chapterChar = extractedInfo.value.characters.find(c => c.id === character.id)
      if (chapterChar) {
        chapterChar.avatar = response.data.avatar
      }

      // 如果是项目人物，更新projectCharacters
      const projectChar = projectCharacters.value.find(c => c.id === character.id)
      if (projectChar) {
        projectChar.avatar = response.data.avatar
      }

      toast.success(actionType === 'regenerate' ? '角色图片已重新生成' : '角色图片已生成')
    } else {
      console.error('生成角色图片失败:', response.message)
      toast.error(response.message || '生成失败')
    }
  } catch (err) {
    console.error('生成角色图片失败:', err)
    toast.error(`生成失败: ${err.message || '未知错误'}`)
  } finally {
    generatingCharacter.value.delete(character.id)
  }
}

const submitBatchGenerateCharacters = async (characterList) => {
  if (!Array.isArray(characterList) || characterList.length === 0) return false
  try {
    if (characterList.some(c => !c?.id) && activeEpisode.value) {
      await loadExtractedInfo(activeEpisode.value)
    }
    const validCharacters = characterList.filter(c => !!c?.id)
    if (validCharacters.length === 0) {
      toast.warning('角色尚未保存完成，请稍后重试')
      return false
    }
    if (validCharacters.length < characterList.length) {
      toast.warning('部分角色尚未保存，已跳过')
    }

    const defaultRes = await modelInstanceApi.getDefault('IMAGE')
    const defaultModel = defaultRes?.data
    if (!defaultModel) {
      toast.error('未找到默认图像生成模型，请先配置模型')
      return false
    }

    const requestList = validCharacters.map(character => ({
      modelInstanceId: defaultModel.id,
      characterId: character.id,
      projectId: projectId,
      description: character.description || `${character.name}的形象描述`
    }))

    validCharacters.forEach(character => generatingCharacter.value.add(character.id))
    await characterApi.batchGenerateImage(requestList)
    toast.success(`已提交 ${requestList.length} 个角色生成任务`)
    return true
  } catch (err) {
    characterList.forEach(character => {
      if (character?.id) generatingCharacter.value.delete(character.id)
    })
    toast.error(err.message || '批量生成提交失败')
    return false
  }
}

// 一键生成：生成所有未出图角色
const generateAllCharacters = async () => {
  const charactersWithoutAvatar = extractedInfo.value.characters.filter(c => !hasRealAvatar(c))
  if (charactersWithoutAvatar.length === 0) {
    toast.warning('所有角色已有图片')
    return
  }
  await submitBatchGenerateCharacters(charactersWithoutAvatar)
}

// 批量生成：生成勾选角色（可含已出图角色，等价重新生成）
const generateSelectedCharacters = async () => {
  const selectedList = extractedInfo.value.characters.filter(c => selectedCharacterIds.value.has(c.id))
  if (selectedList.length === 0) {
    toast.warning('请先勾选角色')
    return
  }
  const ok = await submitBatchGenerateCharacters(selectedList)
  if (ok) {
    clearCharacterSelection()
  }
}

// 生成角色视频
const generateCharacterVideo = async (character) => {
  if (generatingVideo.value.has(character.id)) return
  
  generatingVideo.value.add(character.id)
  videoTasks.value[character.id] = null
  
  try {
    if (videoModels.value.length === 0) {
      await loadVideoModels()
    }
    const modelInstanceId = selectedModel.value || defaultVideoModelId.value
    if (!modelInstanceId) {
      result.value = { success: false, message: '未找到默认视频模型，请先在右侧选择模型' }
      showResult.value = true
      generatingVideo.value.delete(character.id)
      return
    }

    const response = await episodeApi.generateCharacterVideo({
      modelInstanceId,
      episodeId: activeEpisode.value,
      characterId: character.id,
      projectId: projectId
    })
    
    if (response.code === 200 && response.data) {
      // 保存任务对象，用于后续轮询
      const videoTask = response.data
      videoTasks.value[character.id] = videoTask
      // 开始轮询任务状态
      pollVideoTask(character.id, videoTask.id)
    } else {
      console.error('生成角色视频失败:', response.message)
      generatingVideo.value.delete(character.id)
    }
  } catch (err) {
    console.error('生成角色视频失败:', err)
    generatingVideo.value.delete(character.id)
  }
}

// 轮询视频任务状态
const pollVideoTask = (characterId, taskId) => {
  // 清除已有的轮询
  if (videoPollingIntervals.value[characterId]) {
    clearInterval(videoPollingIntervals.value[characterId])
  }
  
  const interval = setInterval(async () => {
    try {
      const response = await videoTaskApi.get(taskId)
      if (response.code === 200 && response.data) {
        const videoTask = response.data
        videoTasks.value[characterId] = videoTask
        
        // status: 0-等待 1-处理中 2-成功 3-失败
        if (videoTask.status === 2) {
          // 视频生成成功
          clearInterval(interval)
          videoPollingIntervals.value[characterId] = null
          generatingVideo.value.delete(characterId)
        } else if (videoTask.status === 3) {
          // 视频生成失败
          clearInterval(interval)
          videoPollingIntervals.value[characterId] = null
          generatingVideo.value.delete(characterId)
          console.error('视频生成失败:', videoTask.errorMessage)
        }
        // status 0 或 1 时继续轮询
      }
    } catch (err) {
      console.error('查询视频任务状态失败:', err)
    }
  }, 2000) // 每 2 秒轮询一次
  
  videoPollingIntervals.value[characterId] = interval
}

// 清除视频轮询
const clearVideoPoll = (characterId) => {
  if (videoPollingIntervals.value[characterId]) {
    clearInterval(videoPollingIntervals.value[characterId])
    videoPollingIntervals.value[characterId] = null
  }
}

const getWsUrl = () => {
  const apiBase = import.meta.env.VITE_API_BASE_URL ?? ''
  if (!apiBase || apiBase.startsWith('/')) {
    const wsProtocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
    const wsPrefix = (apiBase || '').replace(/\/api\/?$/, '')
    return `${wsProtocol}://${window.location.host}${wsPrefix}/webSocket/${projectId}`
  }
  const wsBase = apiBase.replace(/^http/, 'ws')
  return `${wsBase}/webSocket/${projectId}`
}

const isCharacterVideoTask = (taskType) => {
  if (taskType == null) return false
  if (typeof taskType === 'number') return taskType === 1
  if (typeof taskType === 'string') {
    return taskType === 'CHARACTER_VIDEO_GENERATION' || taskType === 'character_video_generation' || taskType === '1'
  }
  if (typeof taskType === 'object') {
    if (taskType.code != null) return taskType.code === 1
    if (taskType.key) return isCharacterVideoTask(taskType.key)
    if (taskType.name) return isCharacterVideoTask(taskType.name)
  }
  return false
}

const updateCharacterVideo = (characterId, videoUrl) => {
  if (!characterId || !videoUrl) return
  const updateList = (list) => {
    const idx = list.findIndex(c => String(c.id) === String(characterId))
    if (idx !== -1) {
      list[idx].videoUrl = videoUrl
      return true
    }
    return false
  }
  updateList(extractedInfo.value.characters)
  updateList(projectCharacters.value)
  if (videoTasks.value[characterId]) {
    videoTasks.value[characterId] = { ...videoTasks.value[characterId], videoUrl, status: 2 }
  }
  generatingVideo.value.delete(characterId)
}

const updateCharacterImageByPayload = (characterPayload) => {
  if (!characterPayload || !characterPayload.id) return
  const characterId = characterPayload.id
  const updateList = (list) => {
    const idx = list.findIndex(c => String(c.id) === String(characterId))
    if (idx !== -1) {
      list[idx] = {
        ...list[idx],
        ...characterPayload,
        avatar: characterPayload.avatar || list[idx].avatar
      }
      return true
    }
    return false
  }
  updateList(extractedInfo.value.characters)
  updateList(projectCharacters.value)
  generatingCharacter.value.delete(characterId)
}

const updateShotVideo = (shotId, videoUrl) => {
  if (!shotId || !videoUrl) return
  const idx = storyboards.value.findIndex(sb => String(sb.id) === String(shotId))
  if (idx !== -1) {
    storyboards.value[idx].videoUrl = videoUrl
  }
  shotVideoTasks.value[shotId] = {
    ...(shotVideoTasks.value[shotId] || {}),
    videoUrl,
    status: 'success'
  }
  generatingShotVideo.value.delete(shotId)
  if (selectedStoryboard.value && String(selectedStoryboard.value.id) === String(shotId)) {
    videoPreviewTab.value = 'edit'
  }
  result.value = { success: true, message: '分镜视频生成完成' }
  showResult.value = true
}

const handleWsMessage = (event) => {
  let payload = event.data
  if (typeof payload === 'string') {
    try {
      payload = JSON.parse(payload)
    } catch {
      return
    }
  }
  if (!payload) return
  if (payload.bizType === 'CHARACTER_IMAGE_BATCH') {
    const batchStatus = String(payload.status || '').toUpperCase()
    if (batchStatus === 'SUCCESS' && payload.character) {
      updateCharacterImageByPayload(payload.character)
      return
    }
    if (batchStatus === 'FAIL') {
      if (payload.targetId != null) {
        generatingCharacter.value.delete(payload.targetId)
      }
      toast.error(payload.errorMessage || '角色图片生成失败')
      return
    }
  }
  const statusRaw = payload.status ?? payload.taskStatus ?? payload.state
  const status = statusRaw != null ? String(statusRaw).toLowerCase() : ''
  const isFail = status === 'fail' || status === 'failed' || status === 'error' || status === 'failure'
  if (isFail) {
    result.value = { success: false, message: payload.errorMessage || '生成失败' }
    showResult.value = true
    return
  }
  if (!payload.videoUrl) return
  if (isCharacterVideoTask(payload.taskType)) {
    updateCharacterVideo(payload.targetId, payload.videoUrl)
  } else if (payload.taskType === 2 || payload.taskType === 'SHOT_VIDEO_GENERATION' || payload.taskType === 'shot_video_generation') {
    updateShotVideo(payload.targetId, payload.videoUrl)
  }
}

const connectWebSocket = () => {
  if (!projectId) return
  if (wsConnection.value) {
    wsConnection.value.close()
  }
  wsManualClose.value = false
  const ws = new WebSocket(getWsUrl())
  ws.onmessage = handleWsMessage
  ws.onopen = () => {
    wsRetryCount.value = 0
  }
  ws.onclose = () => {
    wsConnection.value = null
    if (wsManualClose.value) return
    if (wsReconnectTimer.value) return
    wsReconnectTimer.value = setTimeout(() => {
      wsReconnectTimer.value = null
      wsRetryCount.value += 1
      connectWebSocket()
    }, Math.min(3000 * Math.max(wsRetryCount.value, 1), 15000))
  }
  ws.onerror = () => {
    ws.close()
  }
  wsConnection.value = ws
}

const disconnectWebSocket = () => {
  if (wsReconnectTimer.value) {
    clearTimeout(wsReconnectTimer.value)
    wsReconnectTimer.value = null
  }
  if (wsConnection.value) {
    wsManualClose.value = true
    wsConnection.value.close()
    wsConnection.value = null
  }
}

const openImagePreview = (imageSrc, imageName) => {
  previewImage.value = { url: imageSrc, name: imageName }
  imagePreview.value = true
}

const openVideoPreview = (videoSrc, videoName) => {
  previewVideo.value = { url: videoSrc, name: videoName }
  videoPreview.value = true
}

const saveCharacterInfo = (character) => {
  if (!character || !character.id) return
  const id = String(character.id)
  if (saveCharacterTimers.has(id)) {
    clearTimeout(saveCharacterTimers.get(id))
  }
  const timer = setTimeout(async () => {
    try {
      await characterApi.update({
        id: character.id,
        name: character.name,
        description: character.description
      })
    } catch (err) {
      console.error('更新角色失败', err)
      toast.error(err.message || '角色更新失败')
    } finally {
      saveCharacterTimers.delete(id)
    }
  }, 400)
  saveCharacterTimers.set(id, timer)
}

const openAddCharacterDrawer = () => {
  if (!activeEpisode.value) return
  addCharacterForm.value = { name: '', description: '' }
  addCharacterError.value = ''
  showAddCharacterDrawer.value = true
}

const closeAddCharacterDrawer = () => {
  addCharacterError.value = ''
  showAddCharacterDrawer.value = false
}

const submitAddCharacter = async () => {
  if (!activeEpisode.value) return
  if (addingCharacter.value) return
  if (!addCharacterForm.value.name.trim()) {
    addCharacterError.value = '请填写角色名称'
    return
  }
  addCharacterError.value = ''
  addingCharacter.value = true
  try {
    const res = await characterApi.add({
      episodeId: activeEpisode.value,
      name: addCharacterForm.value.name.trim(),
      description: addCharacterForm.value.description.trim()
    })
    const newChar = res.data || res
    if (newChar && newChar.id) {
      extractedInfo.value.characters.push({
        ...newChar,
        avatar: newChar.avatar || 'data:image/svg+xml,' + encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100"><rect fill="#8B7355" width="100" height="100"/><text fill="#F4E4BA" x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" font-size="40">${(newChar.name || '?').charAt(0)}</text></svg>`),
        videoUrl: newChar.videoUrl || ''
      })
      await loadProjectAssets()
      closeAddCharacterDrawer()
      toast.success('新增角色成功')
    }
  } catch (err) {
    console.error('添加角色失败', err)
    addCharacterError.value = err.message || '添加角色失败'
    toast.error(err.message || '添加角色失败')
  } finally {
    addingCharacter.value = false
  }
}

const openDeleteConfirm = (character) => {
  if (!character || !character.id) return
  deleteConfirm.value = {
    visible: true,
    character,
    error: '',
    loading: false
  }
}

const closeDeleteConfirm = () => {
  deleteConfirm.value = {
    visible: false,
    character: null,
    error: '',
    loading: false
  }
}

const deleteCharacter = async () => {
  const character = deleteConfirm.value.character
  if (!character || !character.id) return
  const id = character.id
  if (deletingCharacters.value.has(id) || deleteConfirm.value.loading) return
  deletingCharacters.value.add(id)
  deleteConfirm.value.loading = true
  try {
    await characterApi.delete(id)
    extractedInfo.value.characters = extractedInfo.value.characters.filter(c => c.id !== id)
    projectCharacters.value = projectCharacters.value.filter(c => c.id !== id)
    if (selectedCharacterIds.value.has(id)) {
      const next = new Set(selectedCharacterIds.value)
      next.delete(id)
      selectedCharacterIds.value = next
    }
    const { [id]: _removed, ...rest } = videoTasks.value
    videoTasks.value = rest
    closeDeleteConfirm()
    toast.success('删除角色成功')
  } catch (err) {
    console.error('删除角色失败', err)
    deleteConfirm.value.error = err.message || '删除角色失败'
  } finally {
    deletingCharacters.value.delete(id)
    deleteConfirm.value.loading = false
  }
}

const generateSceneImage = async (scene, actionType) => {
  if (!scene || !scene.id) return
  const id = scene.id
  if (generatingScene.value.has(id)) return
  generatingScene.value.add(id)
  try {
    // 获取默认图像模型
    const defaultRes = await modelInstanceApi.getDefault('IMAGE')
    const defaultModel = defaultRes?.data
    if (!defaultModel) {
      toast.error('未找到默认图像生成模型，请先配置模型')
      return
    }

    const requestData = {
      modelInstanceId: defaultModel.id,
      sceneId: scene.id,
      projectId: projectId,
      description: scene.description || `${scene.name}的场景描述`
    }

    const response = await episodeApi.generateSceneImage(requestData)
    if (response.code === 200 && response.data) {
      const thumb = response.data.thumbnail || response.data.cover || response.data.imageUrl
      scene.thumbnail = thumb
      scene.description = response.data.description || scene.description
      scene.name = response.data.name || scene.name
      const chapterScene = extractedInfo.value.scenes.find(s => s.id === scene.id)
      if (chapterScene) chapterScene.thumbnail = thumb
      const projScene = projectScenes.value.find(s => s.id === scene.id)
      if (projScene) projScene.thumbnail = thumb
      toast.success(actionType === 'regenerate' ? '场景图片已重新生成' : '场景图片已生成')
    } else {
      toast.error(response.message || '生成失败')
    }
  } catch (err) {
    console.error('生成场景图片失败:', err)
    toast.error(`生成失败: ${err.message || '未知错误'}`)
  } finally {
    generatingScene.value.delete(id)
  }
}

const saveSceneInfo = (scene) => {
  if (!scene || !scene.id) return
  const id = String(scene.id)
  if (saveSceneTimers.has(id)) {
    clearTimeout(saveSceneTimers.get(id))
  }
  const timer = setTimeout(async () => {
    try {
      await sceneApi.update({
        id: scene.id,
        name: scene.name,
        description: scene.description
      })
    } catch (err) {
      console.error('更新场景失败', err)
      toast.error(err.message || '场景更新失败')
    } finally {
      saveSceneTimers.delete(id)
    }
  }, 400)
  saveSceneTimers.set(id, timer)
}

const openAddSceneDrawer = () => {
  if (!activeEpisode.value) return
  addSceneForm.value = { name: '', description: '' }
  addSceneError.value = ''
  showAddSceneDrawer.value = true
}

const closeAddSceneDrawer = () => {
  addSceneError.value = ''
  showAddSceneDrawer.value = false
}

const submitAddScene = async () => {
  if (!activeEpisode.value) return
  if (addingScene.value) return
  if (!addSceneForm.value.name.trim()) {
    addSceneError.value = '请填写场景名称'
    return
  }
  addSceneError.value = ''
  addingScene.value = true
  try {
    const res = await sceneApi.add({
      episodeId: activeEpisode.value,
      name: addSceneForm.value.name.trim(),
      description: addSceneForm.value.description.trim()
    })
    const newScene = res.data || res
    if (newScene && newScene.id) {
      extractedInfo.value.scenes.push({
        ...newScene,
        thumbnail: newScene.thumbnail || ''
      })
      await loadProjectAssets()
      closeAddSceneDrawer()
      toast.success('新增场景成功')
    }
  } catch (err) {
    console.error('添加场景失败', err)
    addSceneError.value = err.message || '添加场景失败'
    toast.error(err.message || '添加场景失败')
  } finally {
    addingScene.value = false
  }
}

const triggerCharacterUpload = (characterId) => {
  const input = document.getElementById(`character-upload-${characterId}`)
  if (input) input.click()
}

const handleCharacterUpload = async (character, event) => {
  const file = event?.target?.files?.[0]
  await uploadCharacterFile(character, file)
  if (event?.target) event.target.value = ''
}

const handleCharacterDragOver = (character) => {
  if (!character?.id) return
  draggingCharacters.value.add(character.id)
}

const handleCharacterDragLeave = (character) => {
  if (!character?.id) return
  draggingCharacters.value.delete(character.id)
}

const handleCharacterDrop = async (character, event) => {
  const file = event?.dataTransfer?.files?.[0]
  await uploadCharacterFile(character, file)
  if (character?.id) draggingCharacters.value.delete(character.id)
}

const uploadCharacterFile = async (character, file) => {
  if (!file || !character?.id) return
  const id = character.id
  if (uploadingCharacters.value.has(id)) return
  uploadingCharacters.value.add(id)
  try {
    const res = await characterApi.upload(id, file)
    if (res.code === 200 && res.data) {
      const avatar = res.data.avatar || res.data.imageUrl
      character.avatar = avatar || character.avatar
      const chapterChar = extractedInfo.value.characters.find(c => c.id === id)
      if (chapterChar) chapterChar.avatar = character.avatar
      const projectChar = projectCharacters.value.find(c => c.id === id)
      if (projectChar) projectChar.avatar = character.avatar
      toast.success('上传成功')
    } else {
      toast.error(res.message || '上传失败')
    }
  } catch (err) {
    console.error('上传角色图片失败', err)
    toast.error(err.message || '上传失败')
  } finally {
    uploadingCharacters.value.delete(id)
  }
}

const openDeleteSceneConfirm = (scene) => {
  if (!scene || !scene.id) return
  deleteSceneConfirm.value = {
    visible: true,
    scene,
    error: '',
    loading: false
  }
}

const closeDeleteSceneConfirm = () => {
  deleteSceneConfirm.value = {
    visible: false,
    scene: null,
    error: '',
    loading: false
  }
}

const deleteScene = async () => {
  const scene = deleteSceneConfirm.value.scene
  if (!scene || !scene.id) return
  const id = scene.id
  if (deletingScenes.value.has(id) || deleteSceneConfirm.value.loading) return
  deletingScenes.value.add(id)
  deleteSceneConfirm.value.loading = true
  try {
    await sceneApi.delete(id)
    extractedInfo.value.scenes = extractedInfo.value.scenes.filter(s => s.id !== id)
    projectScenes.value = projectScenes.value.filter(s => s.id !== id)
    closeDeleteSceneConfirm()
    toast.success('删除场景成功')
  } catch (err) {
    console.error('删除场景失败', err)
    deleteSceneConfirm.value.error = err.message || '删除场景失败'
  } finally {
    deletingScenes.value.delete(id)
    deleteSceneConfirm.value.loading = false
  }
}

const openDeleteStoryboardConfirm = (storyboard) => {
  if (!storyboard) return
  deleteStoryboardConfirm.value = {
    visible: true,
    storyboard,
    error: '',
    loading: false
  }
}

const closeDeleteStoryboardConfirm = () => {
  deleteStoryboardConfirm.value = {
    visible: false,
    storyboard: null,
    error: '',
    loading: false
  }
}

const deleteStoryboard = async () => {
  const sb = deleteStoryboardConfirm.value.storyboard
  if (!sb || !sb.id || deleteStoryboardConfirm.value.loading) return
  deleteStoryboardConfirm.value.loading = true
  try {
    const res = await storyboardApi.deleteShot(sb.id)
    if (res.code === 200) {
      storyboards.value = storyboards.value.filter(s => s.id !== sb.id)
      delete shotVideoTasks.value[sb.id]
      delete shotVideoPollingIntervals.value[sb.id]
      generatingShotVideo.value.delete(sb.id)
      if (selectedStoryboardIndex.value >= storyboards.value.length) {
        selectedStoryboardIndex.value = Math.max(0, storyboards.value.length - 1)
      }
      result.value = { success: true, message: '删除成功' }
      showResult.value = true
      closeDeleteStoryboardConfirm()
    } else {
      deleteStoryboardConfirm.value.error = res.message || '删除失败'
    }
  } catch (err) {
    deleteStoryboardConfirm.value.error = err.message || '删除失败'
  } finally {
    deleteStoryboardConfirm.value.loading = false
  }
}

const saveStoryboardInfo = async (sb) => {
  if (!sb || !sb.id) return
  try {
    const res = await storyboardApi.update({
      ...sb,
      characters: sb.characters || [],
      scenes: sb.scenes || []
    })
    if (res.code === 200 && res.data) {
      const idx = storyboards.value.findIndex(s => String(s.id) === String(sb.id))
      if (idx !== -1) {
        storyboards.value[idx] = res.data
      }
      result.value = { success: true, message: '分镜已更新' }
      showResult.value = true
    } else {
      result.value = { success: false, message: res.message || '分镜更新失败' }
      showResult.value = true
    }
  } catch (err) {
    result.value = { success: false, message: err.message || '分镜更新失败' }
    showResult.value = true
  }
}

const buildStoryboardCharacterList = () => {
  const map = new Map()
  const add = (c) => {
    if (!c || !c.id) return
    map.set(String(c.id), c)
  }
  extractedInfo.value.characters.forEach(add)
  projectCharacters.value.forEach(add)
  return Array.from(map.values())
}

const storyboardCharacterDisplayList = computed(() => storyboardCharacterPicker.value.list || [])

const normalizeCharacterItem = (item) => {
  if (!item) return null
  const id = item.characterId ?? item.id
  if (!id) return null
  return {
    ...item,
    id,
    name: item.name || item.characterName || '未命名角色',
    description: item.description || '',
    avatar: item.avatar || item.thumbnail || ''
  }
}

const normalizeCharacterSuggest = (item) => {
  if (!item) return null
  if (typeof item === 'string') return { name: item, avatar: '' }
  return {
    id: item.characterId ?? item.id ?? null,
    name: item.name || item.characterName || item.text || '',
    avatar: item.avatar || item.thumbnail || ''
  }
}

const executeStoryboardCharacterSearch = async (keyword) => {
  if (!storyboardCharacterPicker.value.visible) return
  if (!keyword) {
    storyboardCharacterPicker.value.list = [...storyboardCharacterBaseList.value]
    return
  }
  try {
    const res = await characterApi.searchInProject(projectId, { keyword, page: 1, size: 50 })
    if (res.code === 200 && res.data) {
      const records = res.data.records || []
      const normalized = records.map(normalizeCharacterItem).filter(Boolean)
      normalized.forEach((item) => {
        if (item?.id) storyboardCharacterFullMap.value.set(String(item.id), item)
      })
      storyboardCharacterPicker.value.list = normalized
    }
  } catch (err) {
    console.error('角色搜索失败', err)
  }
}

const fetchStoryboardCharacterSuggestions = async (prefix) => {
  if (!storyboardCharacterPicker.value.visible || !prefix) {
    storyboardCharacterSuggestions.value = []
    showStoryboardCharacterSuggestDropdown.value = false
    return
  }
  try {
    const res = await characterApi.suggestInProject(projectId, { prefix, size: 8 })
    if (res.code === 200 && Array.isArray(res.data)) {
      const list = res.data.map(normalizeCharacterSuggest).filter(item => item && item.name)
      storyboardCharacterSuggestions.value = list
      showStoryboardCharacterSuggestDropdown.value = list.length > 0
    } else {
      storyboardCharacterSuggestions.value = []
      showStoryboardCharacterSuggestDropdown.value = false
    }
  } catch (err) {
    console.error('角色补全失败', err)
    storyboardCharacterSuggestions.value = []
    showStoryboardCharacterSuggestDropdown.value = false
  }
}

const handleStoryboardCharacterSearchInput = () => {
  const keyword = storyboardCharacterSearchKeyword.value.trim()
  if (storyboardCharacterSearchTimer) clearTimeout(storyboardCharacterSearchTimer)
  if (storyboardCharacterSuggestTimer) clearTimeout(storyboardCharacterSuggestTimer)

  if (!keyword) {
    storyboardCharacterSuggestions.value = []
    showStoryboardCharacterSuggestDropdown.value = false
    storyboardCharacterPicker.value.list = [...storyboardCharacterBaseList.value]
    return
  }

  storyboardCharacterSuggestTimer = setTimeout(() => {
    fetchStoryboardCharacterSuggestions(keyword)
  }, 180)

  storyboardCharacterSearchTimer = setTimeout(() => {
    executeStoryboardCharacterSearch(keyword)
  }, 240)
}

const handleStoryboardCharacterSearchFocus = () => {
  if (storyboardCharacterSuggestions.value.length > 0) {
    showStoryboardCharacterSuggestDropdown.value = true
  }
}

const clearStoryboardCharacterSearch = () => {
  storyboardCharacterSearchKeyword.value = ''
  storyboardCharacterSuggestions.value = []
  showStoryboardCharacterSuggestDropdown.value = false
  storyboardCharacterPicker.value.list = [...storyboardCharacterBaseList.value]
}

const applyStoryboardCharacterSuggestion = (item) => {
  const name = item?.name || ''
  if (!name) return
  storyboardCharacterSearchKeyword.value = name
  showStoryboardCharacterSuggestDropdown.value = false
  executeStoryboardCharacterSearch(name)
}

const openStoryboardCharacterPicker = (sb) => {
  if (!sb) return
  const list = buildStoryboardCharacterList()
  const selected = new Set((sb.characters || []).map(c => String(c.id)))
  storyboardCharacterSearchKeyword.value = ''
  storyboardCharacterBaseList.value = [...list]
  storyboardCharacterFullMap.value = new Map()
  list.forEach((item) => {
    if (item?.id) storyboardCharacterFullMap.value.set(String(item.id), item)
  })
  storyboardCharacterSuggestions.value = []
  showStoryboardCharacterSuggestDropdown.value = false
  storyboardCharacterPicker.value = {
    visible: true,
    storyboardId: sb.id,
    list,
    selected
  }
}

const closeStoryboardCharacterPicker = () => {
  storyboardCharacterSearchKeyword.value = ''
  storyboardCharacterBaseList.value = []
  storyboardCharacterFullMap.value = new Map()
  storyboardCharacterSuggestions.value = []
  showStoryboardCharacterSuggestDropdown.value = false
  if (storyboardCharacterSearchTimer) clearTimeout(storyboardCharacterSearchTimer)
  if (storyboardCharacterSuggestTimer) clearTimeout(storyboardCharacterSuggestTimer)
  storyboardCharacterPicker.value = {
    visible: false,
    storyboardId: null,
    list: [],
    selected: new Set()
  }
}

const toggleStoryboardCharacter = (character) => {
  const id = String(character.id)
  const selected = storyboardCharacterPicker.value.selected
  if (selected.has(id)) {
    selected.delete(id)
    return
  }
  if (selected.size >= 3) {
    result.value = { success: false, message: '最多选择 3 个角色' }
    showResult.value = true
    return
  }
  selected.add(id)
}

const confirmStoryboardCharacters = () => {
  const { storyboardId, list, selected } = storyboardCharacterPicker.value
  const idx = storyboards.value.findIndex(s => String(s.id) === String(storyboardId))
  if (idx !== -1) {
    const selectedList = Array.from(selected)
      .map((id) => storyboardCharacterFullMap.value.get(String(id)))
      .filter(Boolean)
    if (!selectedList.length) {
      const poolMap = new Map()
      ;[...storyboardCharacterBaseList.value, ...list].forEach((item) => {
        if (item?.id) poolMap.set(String(item.id), item)
      })
      Array.from(selected).forEach((id) => {
        const item = poolMap.get(String(id))
        if (item) selectedList.push(item)
      })
    }
    storyboards.value[idx] = { ...storyboards.value[idx], characters: selectedList }
    saveStoryboardInfo(storyboards.value[idx])
  }
  closeStoryboardCharacterPicker()
}

const buildStoryboardSceneList = () => {
  const map = new Map()
  const add = (s) => {
    if (!s || !s.id) return
    map.set(String(s.id), s)
  }
  extractedInfo.value.scenes.forEach(add)
  projectScenes.value.forEach(add)
  return Array.from(map.values())
}

const storyboardSceneDisplayList = computed(() => storyboardScenePicker.value.list || [])

const normalizeSceneItem = (item) => {
  if (!item) return null
  const id = item.sceneId ?? item.id
  if (!id) return null
  return {
    ...item,
    id,
    name: item.name || item.sceneName || '未命名场景',
    description: item.description || '',
    thumbnail: item.thumbnail || ''
  }
}

const normalizeSceneSuggest = (item) => {
  if (!item) return null
  if (typeof item === 'string') return { name: item, thumbnail: '' }
  return {
    id: item.sceneId ?? item.id ?? null,
    name: item.name || item.sceneName || item.text || '',
    thumbnail: item.thumbnail || ''
  }
}

const executeStoryboardSceneSearch = async (keyword) => {
  if (!storyboardScenePicker.value.visible) return
  if (!keyword) {
    storyboardScenePicker.value.list = [...storyboardSceneBaseList.value]
    return
  }
  try {
    const res = await sceneApi.searchInProject(projectId, { keyword, page: 1, size: 50 })
    if (res.code === 200 && res.data) {
      const records = res.data.records || []
      const normalized = records.map(normalizeSceneItem).filter(Boolean)
      normalized.forEach((item) => {
        if (item?.id) storyboardSceneFullMap.value.set(String(item.id), item)
      })
      storyboardScenePicker.value.list = normalized
    }
  } catch (err) {
    console.error('场景搜索失败', err)
  }
}

const fetchStoryboardSceneSuggestions = async (prefix) => {
  if (!storyboardScenePicker.value.visible || !prefix) {
    storyboardSceneSuggestions.value = []
    showStoryboardSceneSuggestDropdown.value = false
    return
  }
  try {
    const res = await sceneApi.suggestInProject(projectId, { prefix, size: 8 })
    if (res.code === 200 && Array.isArray(res.data)) {
      const list = res.data.map(normalizeSceneSuggest).filter(item => item && item.name)
      storyboardSceneSuggestions.value = list
      showStoryboardSceneSuggestDropdown.value = list.length > 0
    } else {
      storyboardSceneSuggestions.value = []
      showStoryboardSceneSuggestDropdown.value = false
    }
  } catch (err) {
    console.error('场景补全失败', err)
    storyboardSceneSuggestions.value = []
    showStoryboardSceneSuggestDropdown.value = false
  }
}

const handleStoryboardSceneSearchInput = () => {
  const keyword = storyboardSceneSearchKeyword.value.trim()
  if (storyboardSceneSearchTimer) clearTimeout(storyboardSceneSearchTimer)
  if (storyboardSceneSuggestTimer) clearTimeout(storyboardSceneSuggestTimer)

  if (!keyword) {
    storyboardSceneSuggestions.value = []
    showStoryboardSceneSuggestDropdown.value = false
    storyboardScenePicker.value.list = [...storyboardSceneBaseList.value]
    return
  }

  storyboardSceneSuggestTimer = setTimeout(() => {
    fetchStoryboardSceneSuggestions(keyword)
  }, 180)

  storyboardSceneSearchTimer = setTimeout(() => {
    executeStoryboardSceneSearch(keyword)
  }, 240)
}

const handleStoryboardSceneSearchFocus = () => {
  if (storyboardSceneSuggestions.value.length > 0) {
    showStoryboardSceneSuggestDropdown.value = true
  }
}

const clearStoryboardSceneSearch = () => {
  storyboardSceneSearchKeyword.value = ''
  storyboardSceneSuggestions.value = []
  showStoryboardSceneSuggestDropdown.value = false
  storyboardScenePicker.value.list = [...storyboardSceneBaseList.value]
}

const applyStoryboardSceneSuggestion = (item) => {
  const name = item?.name || ''
  if (!name) return
  storyboardSceneSearchKeyword.value = name
  showStoryboardSceneSuggestDropdown.value = false
  executeStoryboardSceneSearch(name)
}

const openStoryboardScenePicker = (sb) => {
  if (!sb) return
  const list = buildStoryboardSceneList()
  const selected = new Set((sb.scenes || []).map(s => String(s.id)))
  storyboardSceneSearchKeyword.value = ''
  storyboardSceneBaseList.value = [...list]
  storyboardSceneFullMap.value = new Map()
  list.forEach((item) => {
    if (item?.id) storyboardSceneFullMap.value.set(String(item.id), item)
  })
  storyboardSceneSuggestions.value = []
  showStoryboardSceneSuggestDropdown.value = false
  storyboardScenePicker.value = {
    visible: true,
    storyboardId: sb.id,
    list,
    selected
  }
}

const closeStoryboardScenePicker = () => {
  storyboardSceneSearchKeyword.value = ''
  storyboardSceneBaseList.value = []
  storyboardSceneFullMap.value = new Map()
  storyboardSceneSuggestions.value = []
  showStoryboardSceneSuggestDropdown.value = false
  if (storyboardSceneSearchTimer) clearTimeout(storyboardSceneSearchTimer)
  if (storyboardSceneSuggestTimer) clearTimeout(storyboardSceneSuggestTimer)
  storyboardScenePicker.value = {
    visible: false,
    storyboardId: null,
    list: [],
    selected: new Set()
  }
}

const toggleStoryboardScene = (scene) => {
  const id = String(scene.id)
  const selected = storyboardScenePicker.value.selected
  if (selected.has(id)) {
    if (selected.size <= 1) {
      result.value = { success: false, message: '至少关联 1 个场景' }
      showResult.value = true
      return
    }
    selected.delete(id)
    return
  }
  selected.clear()
  selected.add(id)
}

const confirmStoryboardScenes = () => {
  const { storyboardId, list, selected } = storyboardScenePicker.value
  if (!selected || selected.size === 0) {
    result.value = { success: false, message: '至少关联 1 个场景' }
    showResult.value = true
    return
  }
  const idx = storyboards.value.findIndex(s => String(s.id) === String(storyboardId))
  if (idx !== -1) {
    const selectedList = Array.from(selected)
      .map((id) => storyboardSceneFullMap.value.get(String(id)))
      .filter(Boolean)
    if (!selectedList.length) {
      const poolMap = new Map()
      ;[...storyboardSceneBaseList.value, ...list].forEach((item) => {
        if (item?.id) poolMap.set(String(item.id), item)
      })
      Array.from(selected).forEach((id) => {
        const item = poolMap.get(String(id))
        if (item) selectedList.push(item)
      })
    }
    const finalList = selectedList.length > 0 ? [selectedList[0]] : []
    storyboards.value[idx] = { ...storyboards.value[idx], scenes: finalList }
    saveStoryboardInfo(storyboards.value[idx])
  }
  closeStoryboardScenePicker()
}

const triggerSceneUpload = (sceneId) => {
  const input = document.getElementById(`scene-upload-${sceneId}`)
  if (input) input.click()
}

const applySceneUploadResult = (scene, res) => {
  if (res.code === 200 && res.data) {
    scene.thumbnail = res.data.thumbnail || scene.thumbnail
    scene.description = res.data.description || scene.description
    scene.name = res.data.name || scene.name
    const chapterScene = extractedInfo.value.scenes.find(s => s.id === scene.id)
    if (chapterScene) {
      chapterScene.thumbnail = scene.thumbnail
      chapterScene.description = scene.description
      chapterScene.name = scene.name
    }
    const projScene = projectScenes.value.find(s => s.id === scene.id)
    if (projScene) {
      projScene.thumbnail = scene.thumbnail
      projScene.description = scene.description
      projScene.name = scene.name
    }
    return true
  }
  return false
}

const handleSceneUpload = async (scene, event) => {
  const file = event?.target?.files?.[0]
  await uploadSceneFile(scene, file)
  if (event?.target) event.target.value = ''
}

const handleSceneDragOver = (scene) => {
  if (!scene?.id) return
  draggingScenes.value.add(scene.id)
}

const handleSceneDragLeave = (scene) => {
  if (!scene?.id) return
  draggingScenes.value.delete(scene.id)
}

const handleSceneDrop = async (scene, event) => {
  const file = event?.dataTransfer?.files?.[0]
  await uploadSceneFile(scene, file)
  if (scene?.id) draggingScenes.value.delete(scene.id)
}

const uploadSceneFile = async (scene, file) => {
  if (!file || !scene?.id) return
  const id = scene.id
  if (uploadingScene.value.has(id)) return
  uploadingScene.value.add(id)
  try {
    const res = await sceneApi.upload(id, file)
    if (!applySceneUploadResult(scene, res)) {
      toast.error(res.message || '上传失败')
    } else {
      toast.success('上传成功')
    }
  } catch (err) {
    console.error('上传场景图片失败', err)
    toast.error(err.message || '上传失败')
  } finally {
    uploadingScene.value.delete(id)
  }
}

const storyboards = ref([])
const generatingStoryboard = ref(false)
const selectedStoryboardIndex = ref(0)
const selectedStoryboard = computed(() => storyboards.value[selectedStoryboardIndex.value] || null)
const selectedScene = computed(() => selectedStoryboard.value?.scenes?.[0] || null)
const selectedSceneThumb = computed(() => selectedScene.value ? getSceneThumbnail(selectedScene.value) : '')
const selectedPreviewThumb = computed(() => selectedStoryboard.value?.thumbnail || selectedSceneThumb.value || '')
const videoPreviewTab = ref('media')
const selectedShotVideoUrl = computed(() => {
  const sb = selectedStoryboard.value
  if (!sb) return ''
  const task = shotVideoTasks.value[sb.id]
  return sb.videoUrl || task?.videoUrl || ''
})
const editPreviewPlayer = ref(null)
const videoEditClips = ref([])
const videoEditPlayingIndex = ref(0)
const availableVideoClips = computed(() => {
  return storyboards.value
    .map((sb, idx) => {
      const task = shotVideoTasks.value[sb.id]
      const url = sb.videoUrl || task?.videoUrl
      if (!url) return null
      return {
        id: sb.id,
        url,
        duration: Number(sb.duration || 4),
        title: sb.title || `镜头 ${idx + 1}`,
        thumb: sb.thumbnail || selectedSceneThumb.value || ''
      }
    })
    .filter(Boolean)
})
const videoEditCurrentUrl = computed(() => {
  if (!videoEditClips.value.length) return ''
  const clip = videoEditClips.value[videoEditPlayingIndex.value]
  return clip?.url || ''
})
const videoEditTotalDuration = computed(() => {
  return videoEditClips.value.reduce((acc, clip) => acc + Number(clip.duration || 0), 0)
})
let videoPreloaderEl = null
const warmVideoCache = (url) => {
  if (!url) return
  if (!videoPreloaderEl) {
    videoPreloaderEl = document.createElement('video')
    videoPreloaderEl.preload = 'auto'
    videoPreloaderEl.muted = true
    videoPreloaderEl.playsInline = true
  }
  if (videoPreloaderEl.src !== url) {
    videoPreloaderEl.src = url
    videoPreloaderEl.load()
  }
}
watch(selectedShotVideoUrl, (url) => {
  warmVideoCache(url)
})
const selectedVideoModelName = computed(() => {
  const id = videoGenSettings.value.modelId
  const model = videoModels.value.find(m => m.id === id)
  return model?.instanceName || model?.modelCode || model?.modelName || model?.name || ''
})

const generateFirstFramePrompt = async () => {
  if (!selectedStoryboard.value || generatingFirstFrame.value) return
  try {
    generatingFirstFrame.value = true
    let modelId = defaultTextModelId.value
    if (!textModels.value.length) {
      await loadTextModels()
    }
    if (!modelId) {
      const defRes = await modelInstanceApi.getDefault('TEXT')
      modelId = defRes?.data?.id || null
      defaultTextModelId.value = modelId
    }
    if (!modelId && textModels.value.length > 0) {
      modelId = textModels.value[0].id
    }
    const res = await storyboardApi.generateFirstFrame({
      shotId: selectedStoryboard.value.id,
      projectId: projectId,
      modelInstanceId: modelId
    })
    if (res.code === 200 && res.data) {
      const idx = selectedStoryboardIndex.value
      storyboards.value[idx] = res.data
      result.value = { success: true, message: '提示词已生成' }
      showResult.value = true
    } else {
      result.value = { success: false, message: res.message || '生成提示词失败' }
      showResult.value = true
    }
  } catch (err) {
    console.error('生成首帧提示词失败', err)
    result.value = { success: false, message: err.message || '生成提示词失败' }
    showResult.value = true
  } finally {
    generatingFirstFrame.value = false
  }
}

const generateShotVideoPrompt = async () => {
  if (!selectedStoryboard.value || generatingShotVideoPrompt.value) return
  try {
    generatingShotVideoPrompt.value = true
    let modelId = defaultTextModelId.value
    if (!textModels.value.length) {
      await loadTextModels()
    }
    if (!modelId) {
      const defRes = await modelInstanceApi.getDefault('TEXT')
      modelId = defRes?.data?.id || null
      defaultTextModelId.value = modelId
    }
    if (!modelId && textModels.value.length > 0) {
      modelId = textModels.value[0].id
    }
    const res = await storyboardApi.generateShotVideoPrompt({
      shotId: selectedStoryboard.value.id,
      projectId: projectId,
      modelInstanceId: modelId
    })
    if (res.code === 200 && res.data) {
      const idx = selectedStoryboardIndex.value
      storyboards.value[idx] = res.data
      result.value = { success: true, message: '视频提示词已生成' }
      showResult.value = true
    } else {
      result.value = { success: false, message: res.message || '生成视频提示词失败' }
      showResult.value = true
    }
  } catch (err) {
    console.error('生成视频提示词失败', err)
    result.value = { success: false, message: err.message || '生成视频提示词失败' }
    showResult.value = true
  } finally {
    generatingShotVideoPrompt.value = false
  }
}

const isVideoTaskSuccess = (task) => {
  if (!task) return false
  const status = task.status
  if (status == null) return false
  if (typeof status === 'number') return status === 2
  const s = String(status).toLowerCase()
  return s === 'success' || s === 'completed' || s === 'finished' || s === 'done'
}

const isVideoTaskFailed = (task) => {
  if (!task) return false
  const status = task.status
  if (status == null) return false
  if (typeof status === 'number') return status === 3
  const s = String(status).toLowerCase()
  return s === 'failed' || s === 'error'
}

const generateShotVideo = async () => {
  if (!selectedStoryboard.value) return
  const shotId = selectedStoryboard.value.id
  if (generatingShotVideo.value.has(shotId)) return
  generatingShotVideo.value.add(shotId)
  try {
    if (!videoModels.value.length) {
      await loadVideoModels()
    }
    let modelId = videoGenSettings.value.modelId
    if (!modelId && videoModels.value.length > 0) {
      const def = defaultVideoModelId.value
        ? videoModels.value.find(m => m.id === defaultVideoModelId.value)
        : null
      modelId = def ? def.id : videoModels.value[0].id
      videoGenSettings.value.modelId = modelId
    }
    if (!modelId) {
      result.value = { success: false, message: '未找到视频模型' }
      showResult.value = true
      generatingShotVideo.value.delete(shotId)
      return
    }
    const res = await storyboardApi.generateShotVideo({
      shotId,
      projectId: projectId,
      modelInstanceId: modelId,
      params: {
        duration: Number(videoGenSettings.value.duration)
      }
    })
    if (res.code === 200 && res.data) {
      shotVideoTasks.value[shotId] = res.data
      result.value = { success: true, message: '成功提交生成任务' }
      showResult.value = true
      pollShotVideoTask(shotId, res.data.id)
    } else {
      result.value = { success: false, message: res.message || '生成视频失败' }
      showResult.value = true
      generatingShotVideo.value.delete(shotId)
    }
  } catch (err) {
    console.error('生成分镜视频失败', err)
    result.value = { success: false, message: err.message || '生成视频失败' }
    showResult.value = true
    generatingShotVideo.value.delete(shotId)
  }
}

const addVideoClip = (clip) => {
  if (!clip?.url) return
  const exists = videoEditClips.value.some(c => String(c.id) === String(clip.id))
  if (exists) return
  videoEditClips.value.push({ ...clip })
}

const addAllVideoClips = () => {
  availableVideoClips.value.forEach(addVideoClip)
}

const removeClip = (index) => {
  if (index < 0 || index >= videoEditClips.value.length) return
  videoEditClips.value.splice(index, 1)
  if (videoEditPlayingIndex.value >= videoEditClips.value.length) {
    videoEditPlayingIndex.value = Math.max(0, videoEditClips.value.length - 1)
  }
}

const moveClip = (index, delta) => {
  const target = index + delta
  if (target < 0 || target >= videoEditClips.value.length) return
  const list = videoEditClips.value
  const [item] = list.splice(index, 1)
  list.splice(target, 0, item)
  if (videoEditPlayingIndex.value === index) {
    videoEditPlayingIndex.value = target
  }
}

const startVideoConcat = async () => {
  if (!videoEditClips.value.length) {
    result.value = { success: false, message: '请先添加分镜视频' }
    showResult.value = true
    return
  }
  videoEditPlayingIndex.value = 0
  await nextTick()
  if (editPreviewPlayer.value) {
    editPreviewPlayer.value.currentTime = 0
    editPreviewPlayer.value.play?.()
  }
}

const handleEditVideoEnded = async () => {
  if (videoEditPlayingIndex.value + 1 >= videoEditClips.value.length) return
  videoEditPlayingIndex.value += 1
  await nextTick()
  if (editPreviewPlayer.value) {
    editPreviewPlayer.value.currentTime = 0
    editPreviewPlayer.value.play?.()
  }
}

const saveFirstFramePrompt = async () => {
  if (!selectedStoryboard.value) return
  if (savingFirstFramePrompt.value) return
  const text = firstFramePrompt.value?.trim() || ''
  if (text === (selectedStoryboard.value.imagePrompt || '')) return
  try {
    savingFirstFramePrompt.value = true
    const res = await storyboardApi.updateFirstFramePrompt({
      shotId: selectedStoryboard.value.id,
      projectId: projectId,
      text
    })
    if (res.code === 200 && res.data) {
      const idx = selectedStoryboardIndex.value
      storyboards.value[idx] = res.data
      result.value = { success: true, message: '提示词已保存' }
      showResult.value = true
    } else {
      result.value = { success: false, message: res.message || '保存失败' }
      showResult.value = true
    }
  } catch (err) {
    console.error('保存提示词失败', err)
    result.value = { success: false, message: err.message || '保存失败' }
    showResult.value = true
  } finally {
    savingFirstFramePrompt.value = false
  }
}

const pollShotVideoTask = (shotId, taskId) => {
  if (!taskId) return
  if (shotVideoPollingIntervals.value[shotId]) {
    clearInterval(shotVideoPollingIntervals.value[shotId])
  }
  const interval = setInterval(async () => {
    try {
      const response = await videoTaskApi.get(taskId)
      if (response.code === 200 && response.data) {
        const videoTask = response.data
        shotVideoTasks.value[shotId] = videoTask
        if (isVideoTaskSuccess(videoTask) && videoTask.videoUrl) {
          clearInterval(interval)
          shotVideoPollingIntervals.value[shotId] = null
          generatingShotVideo.value.delete(shotId)
          videoPreviewTab.value = 'edit'
        } else if (isVideoTaskFailed(videoTask)) {
          clearInterval(interval)
          shotVideoPollingIntervals.value[shotId] = null
          generatingShotVideo.value.delete(shotId)
          result.value = { success: false, message: videoTask.errorMessage || '生成视频失败' }
          showResult.value = true
        }
      }
    } catch (err) {
      clearInterval(interval)
      shotVideoPollingIntervals.value[shotId] = null
      generatingShotVideo.value.delete(shotId)
    }
  }, 5000)
  shotVideoPollingIntervals.value[shotId] = interval
}

const generateFirstFrameImage = async () => {
  if (!selectedStoryboard.value || generatingFirstFrameImage.value) return
  try {
    generatingFirstFrameImage.value = true
    if (!imageModels.value.length) {
      await loadImageModels()
    }
    let modelId = defaultImageModelId.value
    if (!modelId && imageModels.value.length > 0) {
      modelId = imageModels.value[0].id
    }
    const res = await storyboardApi.generateFirstFrameImage({
      shotId: selectedStoryboard.value.id,
      projectId: projectId,
      modelInstanceId: modelId
    })
    if (res.code === 200 && res.data) {
      const idx = selectedStoryboardIndex.value
      storyboards.value[idx] = res.data
      result.value = { success: true, message: '首帧图已生成' }
      showResult.value = true
    } else {
      result.value = { success: false, message: res.message || '首帧图生成失败' }
      showResult.value = true
    }
  } catch (err) {
    console.error('生成首帧图失败', err)
    result.value = { success: false, message: err.message || '首帧图生成失败' }
    showResult.value = true
  } finally {
    generatingFirstFrameImage.value = false
  }
}

const openScenePicker = () => {
  showScenePicker.value = true
}

const selectStoryboardScene = (scene) => {
  if (!selectedStoryboard.value) return
  const idx = selectedStoryboardIndex.value
  const current = storyboards.value[idx]
  storyboards.value[idx] = { ...current, scenes: [scene] }
  saveStoryboardInfo(storyboards.value[idx])
  showScenePicker.value = false
}

const triggerSceneReplace = () => {
  if (!selectedScene.value) {
    result.value = { success: false, message: '请先选择场景' }
    showResult.value = true
    return
  }
  sceneReplaceInput.value?.click()
}

const handleSceneReplace = async (event) => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file || !selectedScene.value) return
  try {
    const res = await sceneApi.upload(selectedScene.value.id, file)
    if (res.code === 200 && res.data) {
      const updatedScene = res.data
      // 更新项目场景与分镜场景
      const projectIndex = projectScenes.value.findIndex(s => s.id === updatedScene.id)
      if (projectIndex !== -1) {
        projectScenes.value[projectIndex] = { ...projectScenes.value[projectIndex], ...updatedScene }
      }
      if (selectedStoryboard.value) {
        const idx = selectedStoryboardIndex.value
        const current = storyboards.value[idx]
        const newScenes = (current.scenes || []).map(s => s.id === updatedScene.id ? updatedScene : s)
        storyboards.value[idx] = { ...current, scenes: newScenes }
      }
      result.value = { success: true, message: '背景已替换' }
      showResult.value = true
    } else {
      result.value = { success: false, message: res.message || '替换失败' }
      showResult.value = true
    }
  } catch (err) {
    console.error('替换背景失败', err)
    result.value = { success: false, message: err.message || '替换失败' }
    showResult.value = true
  }
}

const loadStoryboards = async () => {
  if (!activeEpisode.value) return
  try {
    const res = await storyboardApi.get(activeEpisode.value)
    if (res.code === 200 && res.data) {
      storyboards.value = res.data
      if (storyboards.value.length > 0) {
        selectedStoryboardIndex.value = 0
      }
    }
  } catch (err) {
    console.error('加载分镜失败', err)
  }
}

const generateStoryboards = async () => {
  if (!activeEpisode.value || generatingStoryboard.value) return
  generatingStoryboard.value = true
  try {
    let modelId = selectedModel.value
    if (!modelId) {
      const defaultModel = await loadDefaultModel()
      modelId = defaultModel?.id
    }
    const res = await storyboardApi.generate({
      episodeId: activeEpisode.value,
      projectId: projectId,
      modelId
    })
    if (res.code === 200 && res.data) {
      storyboards.value = res.data
      if (storyboards.value.length > 0) {
        selectedStoryboardIndex.value = 0
      }
      result.value = { success: true, message: '分镜已生成' }
      showResult.value = true
    } else {
      result.value = { success: false, message: res.message || '分镜生成失败' }
      showResult.value = true
    }
  } catch (err) {
    console.error('分镜生成失败', err)
    result.value = { success: false, message: err.message || '分镜生成失败' }
    showResult.value = true
  } finally {
    generatingStoryboard.value = false
  }
}

const closeImagePreview = () => {
  imagePreview.value = false
  previewImage.value = { url: '', name: '' }
}

const closeVideoPreview = () => {
  videoPreview.value = false
  previewVideo.value = { url: '', name: '' }
}

const downloadCurrentImage = () => {
  if (previewImage.value.url) {
    downloadImage(previewImage.value.url, previewImage.value.name)
  }
}

const downloadImage = async (imageSrc, imageName) => {
  try {
    const response = await fetch(imageSrc)
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${imageName || 'character'}.png`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (err) {
    console.error('下载失败:', err)
    // 降级方案：直接打开新窗口
    window.open(imageSrc, '_blank')
  }
}

const loadProjectAssets = async () => {
  loadingProjectAssets.value = true
  try {
    const result = await projectApi.getProperty(projectId)
    if (result.code === 200 && result.data) {
      projectCharacters.value = (result.data.characters || []).map(c => ({
        ...c,
        avatar: c.avatar || 'data:image/svg+xml,' + encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><rect fill="#D4AF37" width="48" height="48"/><text fill="#1A1A1E" x="24" y="28" text-anchor="middle" font-size="16" font-weight="600">${c.name?.charAt(0) || '?'}</text></svg>`)
      }))
      projectScenes.value = (result.data.scenes || []).map(s => ({
        ...s,
        thumbnail: s.thumbnail || 'data:image/svg+xml,' + encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 48 48"><rect fill="#8B7355" width="48" height="48"/><text fill="#F4E4BA" x="24" y="28" text-anchor="middle" font-size="10">${s.name?.substring(0, 4) || '场景'}</text></svg>`)
      }))
      // 将项目场景的缩略图合并回当前章节场景列表
      projectScenes.value.forEach(ps => {
        const scene = extractedInfo.value.scenes.find(s => s.id === ps.id)
        if (scene) {
          scene.thumbnail = ps.thumbnail
          scene.description = ps.description || scene.description
          scene.name = ps.name || scene.name
        }
      })
    }
  } catch (err) {
    console.error('获取项目资源失败:', err)
  }
  loadingProjectAssets.value = false
}

const getEpisodeName = (episodeId) => {
  const episode = episodes.value.find(e => e.id === episodeId)
  return episode ? episode.name : '未知章节'
}

const loadDefaultModel = async () => {
  try {
    const result = await modelInstanceApi.getDefault('TEXT')
    if (result.code === 200 && result.data) {
      return result.data
    }
  } catch (err) {
    console.warn('获取默认模型失败:', err)
  }
  return null
}

const loadTextModels = async () => {
  try {
    const result = await modelInstanceApi.list('TEXT', { page: 1, size: 100 })
    if (result.code === 200 && result.data) {
      textModels.value = result.data.records || result.data.modelInstances || result.data || []
      if (textModels.value.length > 0) {
        const defRes = await modelInstanceApi.getDefault('TEXT')
        const defaultId = defRes?.data?.id || null
        defaultTextModelId.value = defaultId
        selectedModel.value = defaultId || textModels.value[0].id
      }
    }
  } catch (err) {
    console.error('加载模型列表失败:', err)
  }
}

const loadImageModels = async () => {
  try {
    const result = await modelInstanceApi.list('IMAGE', { page: 1, size: 100 })
    if (result.code === 200 && result.data) {
      imageModels.value = result.data.records || result.data.modelInstances || result.data || []
      if (imageModels.value.length > 0) {
        const defRes = await modelInstanceApi.getDefault('IMAGE')
        const defaultId = defRes?.data?.id || null
        defaultImageModelId.value = defaultId
        selectedModel.value = defaultId || imageModels.value[0].id
      }
    }
  } catch (err) {
    console.error('加载图像模型列表失败:', err)
  }
}

const loadVideoModels = async () => {
  try {
    const result = await modelInstanceApi.list('VIDEO', { page: 1, size: 100 })
    if (result.code === 200 && result.data) {
      videoModels.value = result.data.records || result.data.modelInstances || result.data || []
      if (videoModels.value.length > 0) {
        const defRes = await modelInstanceApi.getDefault('VIDEO')
        const defaultId = defRes?.data?.id || null
        defaultVideoModelId.value = defaultId
        selectedModel.value = defaultId || videoModels.value[0].id
      }
    }
  } catch (err) {
    console.error('加载视频模型列表失败:', err)
  }
}

const nextStep = async () => {
  if (currentStepNum.value === 0) {
    await saveEpisode()
    try {
      await extractInfo()
    } catch (err) {
      // 提取失败时不进入下一步
      errorMessage.value = err.message || '提取信息失败'
      showErrorModal.value = true
      return
    }
  }
  currentStep.value = (currentStepNum.value + 1)
}

const goToStep = async (index) => {
  if (!activeEpisode.value) return
  if (index > maxReachedStep.value) return
  if (index === currentStepNum.value) return
  currentStep.value = index
}

const skipExtract = async () => {
  await saveEpisode()
  currentStep.value = 1
}

const prevStep = () => {
  currentStep.value = Math.max(0, currentStepNum.value - 1)
}

const extractInfo = async () => {
  if (!activeEpisode.value) return false
  
  extracting.value = true
  globalStore.setLoading(true, '正在分析文本内容...')
  try {
    let modelId = selectedModel.value
    if (!modelId) {
      const defaultModel = await loadDefaultModel()
      modelId = defaultModel?.id
    }
    const result = await episodeApi.extract(activeEpisode.value, modelId)
    // 检查业务码
    if (result.code !== 200) {
      throw new Error(result.message || '提取失败')
    }
    if (result.data) {
      extractedInfo.value = mapExtractedInfo(result.data)
      await loadProjectAssets()
    } else {
      await loadExtractedInfo(activeEpisode.value)
      await loadProjectAssets()
    }
    return true
  } catch (err) {
    console.error('提取信息失败:', err)
    throw err
  } finally {
    extracting.value = false
    globalStore.setLoading(false)
  }
}

const reExtract = async () => {
  await extractInfo()
}

const saveEpisode = async () => {
  if (!activeEpisode.value) return
  
  const contentChanged = currentContent.value !== lastSavedContent.value
  if (!contentChanged && saveStatus.value === 'saved') return
  
   saveStatus.value = 'saving'
   try {
    const episodeMeta = episodes.value.find(e => e.id === activeEpisode.value) || {}
    await episodeApi.update({
      id: activeEpisode.value,
      projectId: Number(projectId),
      chapterNumber: episodeMeta.chapterNumber,
      name: episodeMeta.name,
      content: currentContent.value
    })
     lastSavedContent.value = currentContent.value
     saveStatus.value = 'saved'
  } catch (err) {
    console.error('保存失败:', err)
    saveStatus.value = 'unsaved'
  }
}

const nextChapter = () => {
  const currentIndex = episodes.value.findIndex(e => e.id === activeEpisode.value)
  if (currentIndex < episodes.value.length - 1) {
    selectEpisode(episodes.value[currentIndex + 1].id)
  }
}

const fetchProject = async () => {
  try {
    const result = await projectApi.get(projectId)
    if (result.code === 200 && result.data) {
      const project = result.data
      projectName.value = project.name
      consistency.value = project.consistency || 0
      
      const styleMap = {
        1: { name: '写实风格', preview: 'linear-gradient(135deg, #D4AF37 0%, #B8860B 100%)' },
        2: { name: '动漫风格', preview: 'linear-gradient(135deg, #E8C49A 0%, #CD7F32 100%)' },
        3: { name: '油画质感', preview: 'linear-gradient(135deg, #F4E4BA 0%, #D4AF37 100%)' },
        4: { name: '赛博朋克', preview: 'linear-gradient(135deg, #FFD700 0%, #B8860B 100%)' }
      }
      if (project.style && styleMap[project.style]) {
        currentStyle.value = styleMap[project.style]
      }
    }
  } catch (err) {
    console.error('获取项目失败:', err)
  }
}

const fetchEpisodes = async () => {
  try {
    const result = await episodeApi.list(projectId)
    if (result.code === 200 && result.data) {
      episodes.value = result.data
      if (result.data.length > 0) {
        selectEpisode(result.data[0].id)
      }
    }
  } catch (err) {
    console.error('获取章节列表失败:', err)
  }
}

const currentStyle = ref({
  name: '写实风格',
  preview: 'linear-gradient(135deg, #D4AF37 0%, #B8860B 100%)'
})

watch(currentStep, async (newStep) => {
  if (Number(newStep) > maxReachedStep.value) {
    maxReachedStep.value = Number(newStep)
  }
  if (newStep === 1) {
    loadTextModels()
    // 切换到文本模型时设置默认ѡ
    if (textModels.value.length > 0) {
      const defaultModel = defaultTextModelId.value
        ? textModels.value.find(m => m.id === defaultTextModelId.value)
        : null
      selectedModel.value = defaultModel ? defaultModel.id : textModels.value[0].id
    }
  }
  if (newStep === 2) {
    await loadImageModels()
    // 切换到图像模型时设置默认ѡ
    if (imageModels.value.length > 0) {
      const defaultModel = defaultImageModelId.value
        ? imageModels.value.find(m => m.id === defaultImageModelId.value)
        : null
      selectedModel.value = defaultModel ? defaultModel.id : imageModels.value[0].id
    }
  }
  if (newStep === 3) {
    await loadVideoModels()
    // 切换到视频模型时设置默认ѡ
    if (videoModels.value.length > 0) {
      const defaultModel = defaultVideoModelId.value
        ? videoModels.value.find(m => m.id === defaultVideoModelId.value)
        : null
      selectedModel.value = defaultModel ? defaultModel.id : videoModels.value[0].id
    }
  }
  if (newStep === 4) {
    await loadTextModels()
    if (textModels.value.length > 0) {
      const defaultModel = defaultTextModelId.value
        ? textModels.value.find(m => m.id === defaultTextModelId.value)
        : null
      selectedModel.value = defaultModel ? defaultModel.id : textModels.value[0].id
    }
    await loadStoryboards()
  }
  if (newStep >= 5) {
    leftPanelCollapsed.value = true
    rightPanelCollapsed.value = true
    globalStore.sidebarCollapsed = true
  }
})

watch(
  [activeEpisode, currentStepNum],
  async ([episodeId, step]) => {
    if (!episodeId) return
    if (step >= 4) {
      await loadStoryboards()
    }
  },
  { immediate: true }
)

watch(videoModels, (list) => {
  if (!videoGenSettings.value.modelId && list.length > 0) {
    const def = defaultVideoModelId.value
      ? list.find(m => m.id === defaultVideoModelId.value)
      : null
    videoGenSettings.value.modelId = def ? def.id : list[0].id
  }
}, { immediate: true })

watch(storyboards, (list) => {
  if (!Array.isArray(list) || list.length === 0) {
    firstFramePrompt.value = ''
    return
  }
  if (selectedStoryboardIndex.value == null || selectedStoryboardIndex.value >= list.length) {
    selectedStoryboardIndex.value = 0
  }
  const sb = list[selectedStoryboardIndex.value]
  if (sb) {
    firstFramePrompt.value = sb.imagePrompt || ''
  }
}, { immediate: true })

const selectVideoModel = (model) => {
  videoGenSettings.value.modelId = model.id
  videoModelDropdownOpen.value = false
}

watch(selectedStoryboardIndex, () => {
  const sb = selectedStoryboard.value
  if (!sb) return
  const task = shotVideoTasks.value[sb.id]
  if (task?.videoUrl) {
    videoPreviewTab.value = 'edit'
  } else {
    videoPreviewTab.value = 'media'
  }
  firstFramePrompt.value = sb.imagePrompt || ''
})

watch(selectedStoryboard, (sb) => {
  if (!sb) return
  if (!firstFramePrompt.value) {
    firstFramePrompt.value = sb.imagePrompt || ''
  }
})

onMounted(async () => {
  fetchProject()
  fetchEpisodes()
  loadProjectAssets()
  connectWebSocket()

  // 加载模型数据
  await loadTextModels()
  await loadImageModels()
  await loadVideoModels()

  if (route.query.step) {
    currentStep.value = parseInt(route.query.step)
  }
})

onUnmounted(() => {
  // 清除所有视频轮询
  Object.values(videoPollingIntervals.value).forEach(interval => {
    if (interval) clearInterval(interval)
  })
  Object.values(shotVideoPollingIntervals.value).forEach(interval => {
    if (interval) clearInterval(interval)
  })
  saveCharacterTimers.forEach((t) => clearTimeout(t))
  saveCharacterTimers.clear()
  saveSceneTimers.forEach((t) => clearTimeout(t))
  saveSceneTimers.clear()
})
onUnmounted(() => {
  disconnectWebSocket()
})
</script>

<style lang="scss" scoped>
.workspace {
  height: 100vh;
  background: rgba(13, 11, 8, 0.85);
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.workspace-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-secondary);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-btn {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.15) 0%, rgba(212, 175, 55, 0.08) 100%);
  border: 1px solid rgba(212, 175, 55, 0.3);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 18px;
    height: 18px;
    color: var(--gold-light);
  }

  &:hover {
    background: linear-gradient(135deg, rgba(212, 175, 55, 0.25) 0%, rgba(212, 175, 55, 0.15) 100%);
    border-color: var(--gold-primary);
    box-shadow: 0 0 12px rgba(212, 175, 55, 0.3);
  }
}

.project-title {
  display: flex;
  align-items: center;
  gap: 10px;
  
  .title-h2 {
    color: var(--gold-light);
    font-size: 18px;
    font-weight: 600;
    text-shadow: 0 2px 10px rgba(212, 175, 55, 0.3);
  }
}

.project-episodes {
  padding: 4px 10px;
  font-size: 12px;
  background: var(--bg-glass);
  border-radius: 4px;
  color: var(--text-tertiary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.consistency-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(16, 185, 129, 0.15);
  border: 1px solid rgba(16, 185, 129, 0.3);
  border-radius: 4px;
  color: var(--success);
  font-size: 12px;
  
  svg {
    width: 14px;
    height: 14px;
  }
}

.workspace-content {
  display: flex;
  flex: 1;
  overflow: visible;
  min-height: 0;
  position: relative;
}

.chapters-sidebar {
  width: 220px;
  border-right: 1px solid var(--border-color);
  background: rgba(20, 18, 15, 0.9);
  overflow: visible;
  flex-shrink: 0;
  min-height: 0;
  position: relative;
  transition: width 0.3s ease;

  &.collapsed {
    width: 0;
    overflow: hidden;
    border-right-color: transparent;
  }
  
  &::-webkit-scrollbar {
    width: 8px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(139, 115, 85, 0.5);
    border-radius: 4px;
    
    &:hover {
      background: #8B7355;
    }
  }
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
}

.sidebar-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
}

.add-chapter-btn {
  width: 26px;
  height: 26px;
  border-radius: var(--radius-sm);
  background: var(--gold-gradient);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 14px;
    height: 14px;
    color: #1A1A1E;
  }
}

.chapters-list {
  padding: 8px;
  overflow-y: auto;
  max-height: calc(100% - 64px);
}

.chapter-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
  margin-bottom: 4px;
  
  &:hover {
    background: var(--bg-glass);
  }
  
  &.active {
    background: rgba(212, 175, 55, 0.1);
    border: 1px solid rgba(212, 175, 55, 0.3);
  }
}

.chapter-number {
  font-size: 11px;
  font-weight: 600;
  color: var(--gold-light);
  min-width: 24px;
}

.chapter-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.chapter-name {
  font-size: 12px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chapter-status {
  font-size: 10px;
  
  &.pending { color: var(--text-muted); }
  &.processing { color: var(--warning); }
  &.completed { color: var(--success); }
}

.chapter-delete-btn {
  width: 22px;
  height: 22px;
  border-radius: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all var(--transition-base);
  
  svg {
    width: 12px;
    height: 12px;
    color: var(--text-muted);
  }
  
  &:hover {
    background: rgba(239, 68, 68, 0.15);
    
    svg {
      color: var(--error);
    }
  }
}

.chapter-item:hover .chapter-delete-btn {
  opacity: 1;
}

.main-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.main-header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-secondary);
  flex-shrink: 0;
  position: relative;
}

.workflow-progress {
  display: flex;
  gap: 8px;
  margin: 0 auto;
}

.step-actions-header {
  position: absolute;
  right: 24px;
  top: 50%;
  transform: translateY(-50%);
}

.workflow-step {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 4px;
  background: var(--bg-tertiary);
  border: 1px solid transparent;
  box-sizing: border-box;
  cursor: default;
  
  &.active {
    background: rgba(212, 175, 55, 0.15);
  }

  &.clickable {
    cursor: pointer;
  }

  &.clickable:hover {
    border-color: rgba(212, 175, 55, 0.35);
    box-shadow: 0 0 10px rgba(212, 175, 55, 0.15);
  }
  
  &.completed .step-indicator {
    background: var(--gold-gradient);
    color: #1A1A1E;
  }
}

.step-indicator {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: var(--bg-glass);
  border: 2px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-tertiary);
  
  svg {
    width: 14px;
    height: 14px;
    color: var(--success);
  }
}

.workflow-step.active .step-indicator {
  background: var(--gold-gradient);
  border-color: var(--gold-primary);
  color: #1A1A1E;
}

.step-label {
  font-size: 12px;
  color: var(--text-tertiary);
}

.workflow-step.active .step-label {
  color: var(--gold-light);
}

.step-actions-header {
  display: flex;
  align-items: center;
}

.save-status {
  font-size: 12px;
  color: var(--text-muted);
  
  &.saving { color: var(--warning); }
  &.saved { color: var(--success); }
}

.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  overflow-x: hidden;
  min-height: 0;
  
  &::-webkit-scrollbar {
    width: 10px;
  }
  
  &::-webkit-scrollbar-track {
    background: #1a1815;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #8B7355;
    border-radius: 5px;
    border: 2px solid transparent;
    background-clip: padding-box;
    
    &:hover {
      background: #D4AF37;
    }
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 16px;
  color: var(--text-muted);
  
  svg {
    width: 64px;
    height: 64px;
    opacity: 0.5;
  }
}

.step-content {
  max-width: 900px;
  margin: 0 auto;
  min-height: calc(100vh - 200px);
}

.step-content.video-step {
  max-width: 1400px;
  width: 100%;
}

.editor-container {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  background: var(--bg-tertiary);
}

.editor-actions {
  display: flex;
  gap: 8px;
}

.toolbar-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  font-size: 13px;
  color: var(--text-secondary);
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
  
  svg {
    width: 16px;
    height: 16px;
  }
  
  &:hover {
    background: var(--bg-glass);
    color: var(--text-primary);
  }
}

.editor-textarea {
  width: 100%;
  min-height: 400px;
  padding: 20px;
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
  background: transparent;
  border: none;
  outline: none;
  resize: vertical;
  
  &::placeholder {
    color: var(--text-muted);
  }
}

.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-tertiary);
}

.footer-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.editor-footer .btn {
  padding: 10px 20px;
  border-radius: 4px;
  white-space: nowrap;
}

.word-count {
  font-size: 13px;
  color: var(--text-muted);
}

.extract-step {
  .extract-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .extract-header .btn {
    padding: 10px 20px;
    border-radius: 4px;
    white-space: nowrap;
    height: auto;
  }
}

.extraction-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  gap: 20px;
}

.extraction-spinner {
  width: 48px;
  height: 48px;
  border: 3px solid var(--border-color);
  border-top-color: var(--gold-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.extraction-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.extraction-section {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 20px;
  max-height: 500px;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 2px;
    
    &:hover {
      background: var(--border-hover);
    }
  }
}

.section-header {
  margin-bottom: 16px;
  
  h4 {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 15px;
    font-weight: 600;
    color: var(--text-primary);
    
    svg {
      width: 20px;
      height: 20px;
      color: var(--gold-primary);
    }
  }
}

.extract-characters-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 4px;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 2px;
    
    &:hover {
      background: var(--border-hover);
    }
  }
}

.extract-character-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  transition: all var(--transition-base);
  cursor: default;

  &:hover {
    border-color: var(--border-hover);
    background: var(--bg-card);
  }
}

.extract-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  overflow: hidden;
  flex-shrink: 0;
  background: linear-gradient(145deg, #2C2520 0%, #1C1814 100%);
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.extract-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.extract-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.extract-desc {
  font-size: 12px;
  color: var(--text-tertiary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.character-card {
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-base);
  position: relative;

  &:hover {
    border-color: var(--border-hover);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  }
}

.character-avatar {
  aspect-ratio: 1;
  width: 100%;
  overflow: hidden;
  background: linear-gradient(145deg, #2C2520 0%, #1C1814 100%);
  position: relative;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: top;
    transition: transform 0.3s ease;
  }
  
  .character-card:hover & img {
    transform: scale(1.05);
  }
  
  .generating-overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .avatar-actions {
    position: absolute;
    bottom: 8px;
    right: 8px;
    opacity: 0;
    transition: opacity 0.2s ease;
  }
  
  &:hover .avatar-actions {
    opacity: 1;
  }
}

.avatar-actions-left {
  position: absolute;
  top: 8px;
  left: 8px;
  opacity: 0;
  transition: opacity 0.2s ease;
  z-index: 3;
  pointer-events: none;
}

.avatar-actions-right {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.2s ease;
  z-index: 3;
  pointer-events: none;
}

.character-avatar:hover .avatar-actions-left {
  opacity: 1;
  pointer-events: auto;
}

.character-avatar:hover .avatar-actions-right {
  opacity: 1;
  pointer-events: auto;
}

.character-avatar-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: none;
}

// 场景卡片与人物卡片保持一致，只调整宽߱
.scene-card .character-avatar {
  aspect-ratio: 16 / 9;
}

.scene-card .character-avatar img {
  object-position: center;
}

.scene-drag-hint {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.45);
  color: #f5d77b;
  font-size: 14px;
  font-weight: 600;
  z-index: 4;
  pointer-events: none;
  border: 1px dashed rgba(245, 215, 123, 0.6);
}

.avatar-video-action-fixed {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 2;
}

.character-video-cover {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: top;
}

.text-btn {
  width: auto;
  padding: 4px 8px;
  font-size: 12px;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  white-space: nowrap;
  line-height: 1.2;
}

.avatar-action-btn.text-btn {
  width: auto;
  height: auto;
  padding: 4px 8px;
  white-space: nowrap;
  line-height: 1.2;
}

.video-preview-player {
  max-width: 80vw;
  max-height: 80vh;
  border-radius: var(--radius-lg);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.avatar-action-btn {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: rgba(212, 175, 55, 0.9);
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  
  svg {
    width: 14px;
    height: 14px;
    color: #1A1A1E;
  }
  
  &:hover {
    background: var(--gold-light);
    transform: scale(1.1);
  }
}

.character-info {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.character-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.character-name-input {
  width: 100%;
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  padding: 0;
  margin: 0 0 6px 0;
  outline: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.2s ease;
}

.character-name-input:focus {
  border-bottom: 1px solid var(--primary);
}

.section-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.section-header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.add-character-btn {
  padding: 6px 12px;
  font-size: 12px;
  border-radius: 6px;
}

.character-card.selectedForBatch {
  border-color: var(--gold-primary);
  box-shadow: 0 0 0 1px rgba(212, 175, 55, 0.5), 0 8px 24px rgba(0, 0, 0, 0.35);
}

.card-select-toggle {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 4;
  width: 22px;
  height: 22px;
  border-radius: 6px;
  border: 1px solid rgba(212, 175, 55, 0.45);
  background: rgba(0, 0, 0, 0.45);
  color: var(--gold-primary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;

  svg {
    width: 14px;
    height: 14px;
  }

  &:hover {
    border-color: var(--gold-primary);
    background: rgba(212, 175, 55, 0.2);
  }
}

.delete-character-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.45);
  border: 1px solid rgba(212, 175, 55, 0.6);
  color: #d4af37;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.35);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(6px);
  transition: all 0.2s ease;
}

.delete-character-btn:hover:not(:disabled) {
  background: rgba(239, 83, 80, 0.18);
  border-color: rgba(239, 83, 80, 0.6);
  color: #ef5350;
}

.upload-scene-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.45);
  border: 1px solid rgba(212, 175, 55, 0.6);
  color: #d4af37;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(6px);
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.35);
}

.upload-scene-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  background: rgba(212, 175, 55, 0.24);
  border-color: rgba(212, 175, 55, 0.85);
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.4), inset 0 1px 0 rgba(255, 255, 255, 0.2);
  color: #f3d986;
}

.upload-scene-btn svg {
  width: 14px;
  height: 14px;
}

.scene-upload-input {
  display: none;
}

.drawer-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: flex-end;
  z-index: 2000;
}

.drawer {
  width: 360px;
  max-width: 90vw;
  background: #1f1f24;
  color: var(--text-primary);
  height: 100%;
  padding: 20px;
  box-shadow: -8px 0 24px rgba(0, 0, 0, 0.35);
  display: flex;
  flex-direction: column;
}

.drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.drawer-close {
  border: none;
  background: transparent;
  color: #999;
  font-size: 20px;
  cursor: pointer;
}

.drawer-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 12px;
}

.form-label {
  font-size: 13px;
  color: #ccc;
}

.required {
  color: #ff6b6b;
  margin-left: 4px;
}

.form-input,
.form-textarea {
  width: 100%;
  border: 1px solid #2c2c33;
  background: #15151a;
  color: var(--text-primary);
  border-radius: 8px;
  padding: 10px 12px;
  outline: none;
  transition: border-color 0.2s ease;
}

.form-input:focus,
.form-textarea:focus {
  border-color: var(--primary);
}

.form-error {
  font-size: 12px;
  color: #ff6b6b;
  margin: 4px 0 0;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2100;
}

.modal {
  width: 420px;
  max-width: 92vw;
  background: #1f1f24;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.45);
  color: var(--text-primary);
  padding: 18px 20px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.modal-close {
  border: none;
  background: transparent;
  color: #999;
  font-size: 20px;
  cursor: pointer;
}

.modal-body {
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-secondary);
}

.modal-tip {
  font-size: 12px;
  color: #aaa;
  margin-top: 6px;
}

.modal-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.character-desc {
  font-size: 12px;
  color: var(--text-tertiary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 32px;
}

.character-desc-input {
  width: 100%;
  min-height: 36px;
  padding: 6px 8px;
  font-size: 12px;
  color: var(--text-secondary);
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid transparent;
  border-radius: var(--radius-sm);
  resize: none;
  outline: none;
  transition: all 0.2s ease;
  font-family: inherit;
  line-height: 1.5;
  
  &::placeholder {
    color: var(--text-muted);
    opacity: 0.6;
  }
  
  &:focus {
    background: rgba(255, 255, 255, 0.08);
    border-color: var(--gold-primary);
  }
  
  &:hover:not(:focus) {
    background: rgba(255, 255, 255, 0.06);
  }
}

.character-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(145deg, #2C2520 0%, #1C1814 100%);
  
  svg {
    width: 48px;
    height: 48px;
    color: rgba(212, 175, 55, 0.3);
  }
}

.character-actions {
  padding: 0 12px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.character-action-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  font-size: 12px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, rgba(212, 175, 55, 0.2) 0%, rgba(212, 175, 55, 0.1) 100%);
  border: 1px solid rgba(212, 175, 55, 0.4);
  color: var(--gold-light);
  cursor: pointer;
  transition: all 0.2s ease;
  
  svg {
    flex-shrink: 0;
  }
  
  &:hover:not(:disabled) {
    background: linear-gradient(135deg, rgba(212, 175, 55, 0.3) 0%, rgba(212, 175, 55, 0.2) 100%);
    border-color: var(--gold-primary);
    color: var(--gold-primary);
    box-shadow: 0 0 12px rgba(212, 175, 55, 0.2);
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  &.secondary {
    background: rgba(255, 255, 255, 0.05);
    border-color: rgba(255, 255, 255, 0.1);
    color: var(--text-secondary);
    
    &:hover:not(:disabled) {
      background: rgba(255, 255, 255, 0.1);
      border-color: rgba(255, 255, 255, 0.2);
      color: var(--text-primary);
    }
  }
}

.character-video {
  padding: 0 12px 12px;
}

.character-video-player {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  background: rgba(0, 0, 0, 0.3);
}

.video-generating {
  width: 100%;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  font-size: 12px;
  
  .spinner {
    color: var(--gold-primary);
  }
}

.scenes-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 4px;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 2px;
    
    &:hover {
      background: var(--border-hover);
    }
  }
}

.scene-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: var(--bg-glass);
  border-radius: var(--radius-md);
  font-size: 14px;
  color: var(--text-primary);
}

.scene-icon {
  width: 20px;
  height: 20px;
  color: var(--gold-primary);
  
  svg {
    width: 100%;
    height: 100%;
  }
}

.empty-section, .empty-models, .empty-assets {
  padding: 30px;
  text-align: center;
  color: var(--text-muted);
  font-size: 13px;
}

.empty-generation {
  padding: 40px 20px;
  text-align: center;
  color: var(--text-tertiary);
  
  svg {
    width: 48px;
    height: 48px;
    margin-bottom: 12px;
    opacity: 0.5;
  }
  
  p {
    font-size: 14px;
    margin: 0;
  }
}

.step-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
  gap: 12px;
}

.step-actions .btn {
  min-width: 130px;
  padding: 12px 24px;
  border-radius: 4px;
  white-space: nowrap;
  height: auto;
}

.image-generation {
  margin-top: 20px;
}

.generation-section {
  margin-bottom: 24px;
}

.character-fix-section {
  .generation-section {
    margin-bottom: 32px;
  }
  
  .character-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
  }
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

.generation-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.character-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
  align-items: stretch;
}

.image-generation .character-grid {
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.character-card {
  width: 100%;
  min-width: 0;
}

.gen-tab {
  padding: 6px 14px;
  font-size: 12px;
  color: var(--text-tertiary);
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  cursor: pointer;
  transition: all var(--transition-base);
  
  &.active {
    background: var(--gold-primary);
    color: #1A1A1E;
    border-color: var(--gold-primary);
  }
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
}

.image-placeholder {
  aspect-ratio: 4 / 3;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: var(--text-muted);
  
  svg {
    width: 48px;
    height: 48px;
    opacity: 0.5;
  }
  
  span {
    font-size: 13px;
  }
}

.storyboard-empty, .video-empty {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 80px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  color: var(--text-muted);
  margin-top: 20px;
  
  svg {
    width: 64px;
    height: 64px;
    opacity: 0.5;
  }
  
  span {
    font-size: 14px;
  }
}

.info-panel {
  width: 260px;
  border-left: 1px solid var(--border-color);
  background: rgba(20, 18, 15, 0.9);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: visible;
  min-height: 0;
  position: relative;
  transition: width 0.3s ease;

  &.collapsed {
    width: 0;
    border-left-color: transparent;
  }
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(139, 115, 85, 0.5);
    border-radius: 3px;
    
    &:hover {
      background: #8B7355;
    }
  }
}

.collapse-edge-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 52px;
  border-radius: 0;
  background: transparent;
  border: none;
  color: rgba(225, 210, 170, 0.95);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-base);
  z-index: 6;
  box-shadow: none;
  padding: 0;

  span {
    font-size: 18px;
    font-weight: 700;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.6);
    line-height: 1;
  }

  &:hover {
    color: #f1dfb6;
    background: rgba(34, 29, 22, 0.35);
    border-radius: 8px;
  }
}

.panel-tabs {
  display: flex;
  border-bottom: 1px solid var(--border-color);
}

.panel-tab {
  flex: 1;
  padding: 10px 12px;
  font-size: 12px;
  color: var(--text-tertiary);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all var(--transition-base);
  border-bottom: 2px solid transparent;
  border-radius: 4px 4px 0 0;
  
  &:hover {
    color: var(--text-primary);
  }
  
  &.active {
    color: var(--gold-light);
    border-bottom-color: var(--gold-primary);
  }
}

.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 2px;
    
    &:hover {
      background: var(--border-hover);
    }
  }
}

.panel-section {
  margin-bottom: 16px;
  
  h4 {
    font-size: 12px;
    font-weight: 600;
    color: var(--text-secondary);
    margin-bottom: 8px;
  }
}

.model-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 300px;
  overflow-y: auto;
  padding-right: 4px;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 2px;
    
    &:hover {
      background: var(--border-hover);
    }
  }
}

.model-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: all var(--transition-base);
  
  &:hover {
    border-color: var(--border-hover);
  }
  
  &.selected {
    border-color: var(--gold-primary);
    background: rgba(212, 175, 55, 0.08);
  }
  
  &.is-default {
    border-left: 3px solid var(--gold-primary);
    
    .model-name {
      color: var(--gold-light);
    }
    
    .model-icon {
      background: rgba(212, 175, 55, 0.15);
      
      svg {
        color: var(--gold-light);
      }
    }
  }
}

.model-icon {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-sm);
  background: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 14px;
    height: 14px;
    color: var(--gold-primary);
  }
}

.model-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.model-name {
  font-size: 12px;
  font-weight: 500;
  color: var(--text-primary);
}

.model-provider {
  font-size: 10px;
  color: var(--text-muted);
}

.model-badge {
  padding: 2px 6px;
  font-size: 9px;
  background: rgba(212, 175, 55, 0.2);
  color: var(--gold-light);
  border-radius: 4px;
}

.project-assets-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 250px;
  overflow-y: auto;
  padding-right: 4px;
  
  &::-webkit-scrollbar {
    width: 4px;
  }
  
  &::-webkit-scrollbar-track {
    background: transparent;
  }
  
  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 2px;
    
    &:hover {
      background: var(--border-hover);
    }
  }
}

.asset-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  background: var(--bg-glass);
  border-radius: var(--radius-sm);
}

.asset-avatar {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-full);
  overflow: hidden;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  &.scene-avatar {
    background: var(--bg-tertiary);
    display: flex;
    align-items: center;
    justify-content: center;
    
    svg {
      width: 20px;
      height: 20px;
      color: var(--gold-primary);
    }
  }
}

.asset-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.asset-name {
  font-size: 12px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.asset-episode {
  font-size: 10px;
  color: var(--text-muted);
}

// 图片预览弹窗
.image-preview-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 40px;
}

.image-preview-header {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 12px;
  z-index: 10;
}

.preview-close-btn,
.preview-download-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  
  svg {
    width: 22px;
    height: 22px;
    color: #fff;
  }
  
  &:hover {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.4);
  }
}

.preview-download-btn {
  &:hover {
    background: rgba(212, 175, 55, 0.8);
    border-color: var(--gold-primary);
    
    svg {
      color: #1A1A1E;
    }
  }
}

.image-preview-container {
  max-width: 80vw;
  max-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  
  img {
    max-width: 100%;
    max-height: 80vh;
    object-fit: contain;
    border-radius: var(--radius-lg);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
  }
}

.image-preview-name {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  color: #fff;
  font-size: 16px;
  font-weight: 500;
  text-align: center;
  opacity: 0.8;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  will-change: opacity;
}

.modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-xl);
  width: 100%;
  max-width: 480px;
  overflow: hidden;
  will-change: transform, opacity;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.close-btn {
  width: 28px;
  height: 28px;
  border-radius: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  
  svg {
    width: 18px;
    height: 18px;
    color: var(--text-tertiary);
  }
  
  &:hover {
    background: var(--bg-glass);
  }
}

.modal-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.textarea {
  min-height: 100px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid var(--border-color);
}

.modal-footer .btn {
  padding: 10px 20px;
  border-radius: 4px;
  font-size: 13px;
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.15s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  
  .modal {
    transform: scale(0.95);
  }
}

// Compact button overrides
.workspace .btn {
  padding: 10px 20px;
  font-size: 13px;
  border-radius: 4px;
  white-space: nowrap;
}

.workspace .btn-primary {
  padding: 10px 20px;
  border-radius: 4px;
}

.workspace .btn-secondary {
  padding: 10px 20px;
  border-radius: 4px;
  white-space: nowrap;
}

.workspace .toolbar-btn {
  padding: 8px 12px;
  font-size: 12px;
  border-radius: 4px;
  white-space: nowrap;
}

.workspace .add-chapter-btn {
  border-radius: 4px;
}

.result-modal {
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 24px 32px;
  min-width: 320px;
  max-width: 420px;
}


.result-content { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.result-icon { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; flex-shrink: 0; svg { width: 24px; height: 24px; } &.success { background: rgba(16, 185, 129, 0.15); svg { color: var(--success); } } &.error { background: rgba(239, 68, 68, 0.15); svg { color: var(--error); } } }
.result-text { text-align: left; flex: 1; min-width: 0; h3 { font-size: 16px; font-weight: 600; margin-bottom: 4px; } p { font-size: 14px; color: var(--text-tertiary); line-height: 1.5; word-break: break-all; } }
.btn-block { width: 100%; padding: 10px; }

.modal-enter-active {
  transition: opacity 0.25s ease;
  
  .modal {
    transition: transform 0.25s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.25s ease;
  }
}

.modal-leave-active {
  transition: opacity 0.2s ease;
  
  .modal {
    transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.2s ease;
  }
}

.modal-enter-from {
  opacity: 0;
  
  .modal {
    transform: scale(0.92) translateY(-10px);
    opacity: 0;
  }
}

.modal-leave-to {
  opacity: 0;
  
  .modal {
    transform: scale(0.95) translateY(5px);
    opacity: 0;
  }
}

.storyboard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.storyboard-actions {
  display: flex;
  gap: 10px;
}

.storyboard-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 16px;
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  max-height: 520px;
  overflow: auto;
}

.storyboard-item {
  display: grid;
  grid-template-columns: 36px 1fr;
  gap: 12px;
  padding: 14px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  position: relative;
}

.storyboard-delete {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 24px;
  height: 24px;
  height: 26px;
  border-radius: 6px;
  border: 1px solid rgba(212, 175, 55, 0.2);
  background: rgba(18, 14, 10, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.storyboard-delete svg {
  width: 14px;
  height: 14px;
}

.storyboard-delete:hover {
  border-color: rgba(239, 68, 68, 0.55);
  color: #ffb4b4;
  background: rgba(239, 68, 68, 0.15);
}

.storyboard-index {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(212, 175, 55, 0.15);
  color: var(--gold-light);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.storyboard-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
}

.storyboard-text {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 12px 44px 12px 12px;
  line-height: 1.6;
  color: var(--text-primary);
}

.storyboard-textarea {
  width: 100%;
  min-height: 64px;
  resize: vertical;
  font-family: inherit;
  font-size: 13px;
  color: var(--text-primary);
  background: transparent;
}

.storyboard-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.storyboard-meta .meta-row {
  display: grid;
  grid-template-columns: 70px 1fr;
  gap: 10px;
  align-items: center;
  padding: 6px 8px;
  border-radius: 6px;
  border: 1px solid transparent;
  box-sizing: border-box;
}

.storyboard-meta .meta-label {
  width: 70px;
}

.storyboard-meta .meta-row.clickable {
  cursor: pointer;
  transition: all 0.2s ease;
}

.storyboard-meta .meta-row.clickable:hover {
  background: rgba(212, 175, 55, 0.08);
  border-color: rgba(212, 175, 55, 0.25);
}

.storyboard-meta .meta-row-characters .tag {
  background: rgba(212, 175, 55, 0.18);
  border-color: rgba(212, 175, 55, 0.55);
  color: #f6e1a6;
}

.storyboard-meta .meta-row-scenes .tag {
  background: rgba(139, 115, 85, 0.22);
  border-color: rgba(139, 115, 85, 0.6);
  color: #f0d7b2;
}

.character-picker-modal {
  width: 1200px !important;
  max-width: 92vw !important;
  min-width: 1100px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.character-picker-search {
  position: relative;
  display: flex;
  align-items: center;
  width: 360px;
  max-width: 100%;
  height: 38px;
  margin: 2px 6px 14px;
  border: 1px solid rgba(212, 175, 55, 0.26);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.03);
  transition: all 0.2s ease;
}

.character-picker-search:focus-within {
  border-color: rgba(212, 175, 55, 0.52);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.12);
}

.character-picker-search-icon {
  width: 15px;
  height: 15px;
  margin-left: 12px;
  color: var(--text-muted);
  flex: 0 0 auto;
}

.character-picker-search-input {
  flex: 1;
  height: 100%;
  background: transparent;
  border: none;
  color: var(--text-primary);
  font-size: 13px;
  padding: 0 8px;
  outline: none;
}

.character-picker-search-input::placeholder {
  color: var(--text-muted);
}

.character-picker-search-clear {
  width: 24px;
  height: 24px;
  margin-right: 8px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--text-tertiary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.character-picker-search-clear:hover {
  color: var(--gold-light);
  background: rgba(212, 175, 55, 0.14);
}

.character-picker-search-clear svg {
  width: 13px;
  height: 13px;
}

.character-picker-suggest-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  z-index: 22;
  background: #17120d;
  border: 1px solid rgba(212, 175, 55, 0.26);
  border-radius: 10px;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.42);
  padding: 6px;
  max-height: 220px;
  overflow-y: auto;
}

.character-picker-suggest-item {
  width: 100%;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--text-secondary);
  text-align: left;
  font-size: 13px;
  padding: 8px 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 10px;
}

.character-picker-suggest-item:hover {
  background: rgba(212, 175, 55, 0.12);
  color: var(--gold-light);
}

.character-picker-suggest-avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid rgba(212, 175, 55, 0.28);
  background: rgba(255, 255, 255, 0.06);
}

.character-picker-suggest-text {
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.character-picker-suggest-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.character-picker-grid {
  display: flex;
  gap: 16px;
  max-height: 60vh;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 4px 6px 12px;
}

.character-card {
  border-radius: 10px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);
  text-align: left;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.character-picker-modal .character-card {
  min-width: 240px;
  max-width: 240px;
}

.character-card.selected {
  border-color: var(--gold-primary);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.2);
}

.card-avatar img {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}

.card-avatar-placeholder {
  width: 100%;
  height: 140px;
  border-radius: 8px;
  background: rgba(212, 175, 55, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: var(--gold-light);
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.card-name {
  font-weight: 600;
  color: var(--text-primary);
}

.card-desc {
  font-size: 12px;
  color: var(--text-tertiary);
  line-height: 1.4;
  max-height: 34px;
  overflow: hidden;
}

.select-limit {
  font-size: 12px;
  color: var(--text-tertiary);
}

.character-picker-modal .modal-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.character-picker-modal .footer-actions {
  display: flex;
  gap: 8px;
}

.character-picker-empty {
  margin: 8px 6px 0;
  font-size: 13px;
  color: var(--text-tertiary);
}

.storyboard-scene-picker-modal {
  width: 1100px !important;
  max-width: 92vw !important;
  min-width: 960px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.storyboard-scene-picker-modal .scene-select-grid {
  display: flex;
  gap: 16px;
  max-height: 60vh;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 4px 6px 12px;
}

.storyboard-scene-picker-modal .scene-select-card {
  min-width: 260px;
  max-width: 260px;
  border-radius: 10px;
  border: 1px solid var(--border-color);
  background: var(--bg-card);
  text-align: left;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: all 0.2s ease;
  cursor: pointer;
}

.storyboard-scene-picker-modal .scene-select-card.selected {
  border-color: var(--gold-primary);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.2);
}

.storyboard-scene-picker-modal .scene-select-thumb {
  width: 100%;
  height: 140px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--bg-tertiary);
}

.storyboard-scene-picker-modal .scene-select-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* override generic meta-row to avoid conflicts inside storyboard meta */
.meta-row {
  display: flex;
}

.meta-label {
  color: var(--text-tertiary);
  font-size: 12px;
}

.meta-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(212, 175, 55, 0.12);
  border: 1px solid rgba(212, 175, 55, 0.35);
  color: var(--gold-light);
  font-size: 12px;
}

.tag.muted {
  background: rgba(255, 255, 255, 0.06);
  border-color: var(--border-color);
  color: var(--text-tertiary);
}

.video-step .storyboard-actions .btn {
  white-space: nowrap;
}

.video-layout {
  display: grid;
  grid-template-columns: 260px minmax(0, 2.2fr) 320px;
  gap: 14px;
  margin: 16px 0 20px;
}

.video-edit-step .video-editor-layout {
  display: grid;
  grid-template-columns: minmax(0, 2fr) 300px;
  gap: 14px;
  margin: 16px 0 16px;
}

.video-edit-step .editor-preview,
.video-edit-step .editor-clips {
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 12px;
  display: flex;
  flex-direction: column;
}

.video-edit-step .editor-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.video-edit-step .editor-tabs .tab {
  flex: 1;
  padding: 8px 10px;
  text-align: center;
  font-size: 12px;
  color: var(--text-tertiary);
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid var(--border-color);
  border-radius: 999px;
}

.video-edit-step .editor-tabs .tab.active {
  color: var(--gold-light);
  border-color: rgba(212, 175, 55, 0.35);
  background: rgba(212, 175, 55, 0.12);
}

.video-edit-step .editor-canvas {
  flex: 1;
  min-height: 260px;
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.video-edit-step .editor-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.video-edit-step .editor-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-top: 10px;
}

.video-edit-step .editor-clips .add-all-btn {
  align-self: flex-start;
  margin-bottom: 10px;
}

.video-edit-step .clip-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
  overflow-y: auto;
  max-height: 420px;
}

.video-edit-step .clip-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.video-edit-step .clip-card:hover {
  border-color: var(--gold-primary);
  box-shadow: 0 0 0 1px rgba(212, 175, 55, 0.25);
}

.video-edit-step .clip-thumb {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 6px;
}

.video-edit-step .clip-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-edit-step .clip-duration {
  position: absolute;
  right: 6px;
  bottom: 6px;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
}

.video-edit-step .clip-title {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.video-edit-step .clip-empty {
  color: var(--text-tertiary);
  font-size: 12px;
  text-align: center;
  padding: 12px 0;
}

.video-edit-step .editor-timeline {
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 12px;
}

.video-edit-step .timeline-header {
  display: flex;
  justify-content: space-between;
  color: var(--text-secondary);
  font-size: 12px;
  margin-bottom: 8px;
}

.video-edit-step .timeline-track {
  background: rgba(255, 255, 255, 0.03);
  border: 1px dashed var(--border-color);
  border-radius: 10px;
  padding: 10px;
}

.video-edit-step .timeline-empty {
  color: var(--text-tertiary);
  font-size: 12px;
  text-align: center;
  padding: 14px 0;
}

.video-edit-step .timeline-clips {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.video-edit-step .timeline-clip {
  display: grid;
  grid-template-columns: 28px 1fr 52px auto;
  align-items: center;
  gap: 8px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 8px;
}

.video-edit-step .clip-actions {
  display: flex;
  gap: 4px;
}

.video-edit-step .clip-actions .icon-btn {
  width: 26px;
  height: 26px;
  border-radius: 6px;
  border: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.03);
  color: var(--text-secondary);
}

.video-edit-step .clip-actions .icon-btn.danger {
  color: #ff7b7b;
  border-color: rgba(255, 123, 123, 0.4);
}

.video-sidebar,
.video-config {
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 12px;
  height: 560px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.video-panel-title {
  font-weight: 600;
  margin-bottom: 10px;
}

.video-config-tabs {
  display: flex;
  gap: 6px;
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 6px;
  margin-bottom: 10px;
}

.video-config-tab {
  flex: 1;
  padding: 6px 10px;
  font-size: 12px;
  color: var(--text-tertiary);
  border: 1px solid transparent;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  transition: all var(--transition-base);
}

.video-config-tab.active {
  color: var(--gold-light);
  border-color: rgba(212, 175, 55, 0.4);
  background: rgba(212, 175, 55, 0.12);
}

.shot-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: auto;
}

.shot-item {
  display: grid;
  grid-template-columns: 28px 1fr;
  gap: 10px;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-card);
  cursor: pointer;
  transition: all var(--transition-base);
}

.shot-item.active {
  border-color: var(--gold-primary);
  box-shadow: 0 0 0 1px rgba(212, 175, 55, 0.3);
}

.shot-index {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: rgba(212, 175, 55, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--gold-light);
}

.shot-text {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
  max-height: 42px;
  overflow: hidden;
}

.shot-empty {
  padding: 12px;
  color: var(--text-tertiary);
  font-size: 12px;
}

.video-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.preview-tabs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  overflow: hidden;
}

.preview-tabs .tab {
  padding: 8px 10px;
  text-align: center;
  font-size: 12px;
  color: var(--text-tertiary);
}

.preview-tabs .tab.active {
  color: var(--gold-light);
  background: rgba(212, 175, 55, 0.12);
}

.preview-canvas {
  flex: 1;
  background: var(--bg-glass);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  aspect-ratio: 16 / 9;
  min-height: 320px;
  max-height: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
}

.preview-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
  color: var(--text-tertiary);
}

.preview-footer {
  display: flex;
  gap: 8px;
}

.meta-chip {
  padding: 6px 10px;
  border-radius: 999px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  font-size: 12px;
  color: var(--text-tertiary);
}

.config-section {
  margin-bottom: 12px;
}

.config-label {
  font-size: 12px;
  color: var(--text-tertiary);
  margin-bottom: 8px;
}

.config-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.config-label-row .config-label {
  margin-bottom: 0;
}

.config-label-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.config-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.video-gen-section .config-card {
  gap: 10px;
}

.config-thumb-wrap {
  width: 100%;
}

.video-gen-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  box-sizing: border-box;
}

.video-gen-block {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.video-gen-label {
  font-size: 12px;
  color: var(--text-tertiary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.video-gen-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.video-gen-value {
  color: var(--text-secondary);
  font-size: 12px;
}

.video-gen-slider {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-secondary);
  font-size: 12px;
}

.video-gen-slider input {
  flex: 1;
}

.video-gen-ref {
  display: flex;
  justify-content: flex-start;
}

.video-gen-ref-box {
  width: 100%;
  height: 120px;
  border: 1px dashed var(--border-color);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.03);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: pointer;
}

.video-gen-select {
  position: relative;
}

.video-gen-select-trigger {
  width: 100%;
  padding: 10px 12px;
  background: rgba(20, 18, 15, 0.9);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: var(--text-primary);
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  cursor: pointer;
  transition: all var(--transition-base);
}

.video-gen-select-trigger svg {
  width: 14px;
  height: 14px;
  color: var(--gold-light);
}

.video-gen-select-trigger:hover {
  border-color: var(--gold-primary);
  box-shadow: 0 0 0 2px rgba(212, 175, 55, 0.15);
}

.video-gen-select-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: rgba(18, 15, 12, 0.98);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 6px;
  z-index: 10;
  box-shadow: 0 16px 30px rgba(0, 0, 0, 0.4);
  max-height: 220px;
  overflow-y: auto;
}

.video-gen-select-item {
  width: 100%;
  text-align: left;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 10px;
  padding: 8px 10px;
  color: var(--text-primary);
  cursor: pointer;
  transition: all var(--transition-base);
}

.video-gen-select-item .item-title {
  font-size: 12px;
  font-weight: 600;
}

.video-gen-select-item .item-sub {
  font-size: 11px;
  color: var(--text-tertiary);
  margin-top: 2px;
}

.video-gen-select-item:hover,
.video-gen-select-item.active {
  border-color: rgba(212, 175, 55, 0.5);
  background: rgba(212, 175, 55, 0.12);
}

.video-gen-card .config-textarea {
  min-height: 120px;
}

.video-gen-ref-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-gen-ref-empty {
  font-size: 12px;
  color: var(--text-tertiary);
}

.config-thumb {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.config-empty {
  height: 120px;
  border-radius: 8px;
  background: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-tertiary);
  font-size: 12px;
}

.config-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.scene-picker-modal {
  max-width: 720px;
}

.scene-picker-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.scene-picker-item {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 8px;
  text-align: left;
  cursor: pointer;
  transition: all var(--transition-base);
}

.scene-picker-item:hover {
  border-color: var(--gold-primary);
  box-shadow: 0 0 0 1px rgba(212, 175, 55, 0.25);
}

.scene-picker-thumb {
  width: 100%;
  aspect-ratio: 16 / 9;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 6px;
}

.scene-picker-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.scene-picker-name {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.scene-picker-empty {
  text-align: center;
  color: var(--text-tertiary);
  padding: 12px 0;
}

.hidden-input {
  display: none;
}

.config-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.config-character-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.config-character-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(139, 115, 85, 0.4);
}

.config-character-avatar {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #8B7355;
  color: #F4E4BA;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.config-character-name {
  font-size: 12px;
  color: var(--text-secondary);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.config-textarea {
  width: 100%;
  min-height: 120px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 10px;
  color: var(--text-secondary);
  font-size: 12px;
  resize: none;
}

.config-actions-footer {
  margin-top: auto;
  display: flex;
  gap: 10px;
}

.video-config .btn,
.video-config .btn-sm {
  padding: 7px 12px;
  font-size: 13px;
  line-height: 1.2;
}

.video-config .config-actions .btn,
.video-config .config-label-row .btn,
.video-config .config-actions-footer .btn {
  max-width: 100%;
  white-space: nowrap;
}

.video-config .config-actions-footer .btn {
  flex: 1;
  justify-content: center;
}
</style>
.config-thumb {
  cursor: pointer;
}

.config-character-avatar img {
  cursor: pointer;
}









